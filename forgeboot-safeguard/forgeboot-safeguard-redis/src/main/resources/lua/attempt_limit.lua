---
--- Created by gewuyou.
--- DateTime: 2025/9/22 10:58
--- attempt_limit.lua
-- KEYS:
--  1: attempts_key   失败计数（窗口内）
--  2: lock_key       锁定键（PX 过期，值=锁到期的服务器毫秒时间戳，仅信息用途）
--  3: strikes_key    累计失败（长期）
--
-- ARGV:
--  1: window_ms
--  2: capacity
--  3: base_lock_ms
--  4: escalate_csv   e.g. "3=300000;5=1800000"
--  5: is_success     1/0
--  6: strike_inc     1/0
--  7: peek_only      1/0 (可选，默认0)
--
-- RETURN (array of 5):
--  [ allowed(0/1),
--    attempts_ttl_ms,
--    lock_ttl_ms,
--    remaining_attempts,
--    capacity ]

local attempts_key = KEYS[1]
local lock_key     = KEYS[2]
local strikes_key  = KEYS[3]

local window_ms    = tonumber(ARGV[1] or "0")
local capacity     = tonumber(ARGV[2] or "0")
local base_lock_ms = tonumber(ARGV[3] or "0")
local escalate_csv = tostring(ARGV[4] or "")
local is_success   = tonumber(ARGV[5] or "0")
local strike_inc   = tonumber(ARGV[6] or "0")
local peek_only    = tonumber(ARGV[7] or "0")

local function now_ms()
  local t = redis.call('TIME')
  return (tonumber(t[1]) * 1000) + math.floor(tonumber(t[2]) / 1000)
end

local function pttl_safe(key)
  local ttl = redis.call('PTTL', key)
  if ttl and ttl > 0 then return ttl else return 0 end
end

-- 解析 "a=b;c=d" / "a=b,c=d" 为 map[threshold]=ms
-- 解析 "a:b,c:d" 或 "a=b;c=d" 为 map[threshold]=ms
local function parse_escalate(csv)
  local m = {}
  if not csv or csv == "" then return m end
  -- 统一分隔符：把分号替换成逗号，便于 split
  local s = csv:gsub(";", ",")
  for pair in string.gmatch(s, "([^,]+)") do
    -- 支持 ":" 或 "="，并忽略两侧空白
    local th, ms = string.match(pair, "^%s*(%d+)%s*[:=]%s*(%d+)%s*$")
    if th and ms then
      m[tonumber(th)] = tonumber(ms)
    end
  end
  return m
end


local function compute_lock_ms(base_ms, esc_map, strikes)
  local ms = base_ms
  if esc_map then
    for th, dur in pairs(esc_map) do
      if strikes >= th and dur > ms then
        ms = dur
      end
    end
  end
  return ms
end

-- 成功：清 attempts / lock；strikes 是否清除取决于业务，这里不动
if is_success == 1 then
  redis.call('DEL', attempts_key)
  redis.call('DEL', lock_key)
  -- 返回“满”的语义给调用侧也行，但 Kotlin 已直接构造，所以这里返回 0 0 0 capacity
  return { 1, 0, 0, capacity, capacity }
end

-- 若存在锁：直接拒绝，并报告锁 TTL + 当前窗口剩余（信息用）
local lock_ttl = pttl_safe(lock_key)
if lock_ttl > 0 then
  -- attempts 剩余次数：窗口内剩余失败额度（不一定有键）
  local attempts = tonumber(redis.call('GET', attempts_key) or "0")
  local remaining = capacity - attempts
  if remaining < 0 then remaining = 0 end
  return { 0, 0, lock_ttl, remaining, capacity }
end

local attempts = tonumber(redis.call('GET', attempts_key) or "0")
local attempts_ttl = pttl_safe(attempts_key)

-- 预检：不消耗窗口，仅判断
if peek_only == 1 then
  if attempts < capacity then
    local remaining = capacity - attempts
    return { 1, attempts_ttl, 0, remaining, capacity }
  else
    -- 窗口已耗尽：可选立即累计 strikes + 加锁
    if strike_inc == 1 then
      local s = tonumber(redis.call('INCR', strikes_key))
      -- 30 天不过期（如无 TTL），按需调整
      local sttl = pttl_safe(strikes_key)
      if sttl == 0 then redis.call('PEXPIRE', strikes_key, 30 * 24 * 60 * 60 * 1000) end

      local esc = parse_escalate(escalate_csv)
      local new_lock_ms = compute_lock_ms(base_lock_ms, esc, s)

      -- 仅延长不缩短
      local cur_ttl = pttl_safe(lock_key)
      if cur_ttl < new_lock_ms then
        redis.call('PSETEX', lock_key, new_lock_ms, tostring(now_ms() + new_lock_ms))
        lock_ttl = new_lock_ms
      else
        lock_ttl = cur_ttl
      end
    end
    return { 0, attempts_ttl, lock_ttl, 0, capacity }
  end
end

-- 真正失败：消耗一次
local new_attempts = attempts + 1

if new_attempts <= capacity then
  -- 第一次写入需要设置 TTL；已有键但无 TTL 也补上
  if attempts == 0 then
    redis.call('PSETEX', attempts_key, window_ms, tostring(new_attempts))
  else
    redis.call('INCR', attempts_key)
    if attempts_ttl == 0 and window_ms > 0 then
      redis.call('PEXPIRE', attempts_key, window_ms)
      attempts_ttl = window_ms
    end
  end

  local remaining = capacity - new_attempts
  attempts_ttl = pttl_safe(attempts_key)
  return { 1, attempts_ttl, 0, remaining, capacity }
else
  -- 窗口耗尽：累计 strikes 并加锁
  local s = tonumber(redis.call('INCR', strikes_key))
  local sttl = pttl_safe(strikes_key)
  if sttl == 0 then redis.call('PEXPIRE', strikes_key, 30 * 24 * 60 * 60 * 1000) end

  local esc = parse_escalate(escalate_csv)
  local new_lock_ms = compute_lock_ms(base_lock_ms, esc, s)

  local cur_ttl = pttl_safe(lock_key)
  if cur_ttl < new_lock_ms then
    redis.call('PSETEX', lock_key, new_lock_ms, tostring(now_ms() + new_lock_ms))
    lock_ttl = new_lock_ms
  else
    lock_ttl = cur_ttl
  end

  return { 0, 0, lock_ttl, 0, capacity }
end
