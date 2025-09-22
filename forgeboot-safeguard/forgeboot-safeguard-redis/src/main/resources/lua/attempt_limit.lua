---
--- Created by gewuyou.
--- DateTime: 2025/9/22 10:58
--- attempt_limit.lua
-- KEYS[1]=zset attempts::<scope>::<key>
-- KEYS[2]=string lock::<scope>::<key>
-- KEYS[3]=string strikes::<principal>
-- ARGV[1]=window_ms
-- ARGV[2]=max
-- ARGV[3]=lock_ms
-- ARGV[4]=escalate_csv
-- ARGV[5]=is_success  "1"=登录成功清理
-- ARGV[6]=strike_inc  "1"=本次超标时累计 strikes；"0"=不累计（多 scope 只记一次）
-- ARGV[7]=peek_only  "1"=仅查看，不写入窗口；"0"=正常失败路径：写入一次失败到窗口

-- 统一时间源：Redis TIME（秒、微秒）
local t = redis.call('TIME')
local now = t[1] * 1000 + math.floor(t[2] / 1000)

local window_ms = tonumber(ARGV[1])

-- 成功清理：attempts/lock/ctr 一并清掉
if ARGV[5] == "1" then
  redis.call('DEL', KEYS[1])
  redis.call('DEL', KEYS[2])
  redis.call('DEL', KEYS[1]..":ctr")
  return {1, 0, 0}
end

-- 仍在锁内？
local lock_ttl = redis.call('PTTL', KEYS[2])
if lock_ttl and lock_ttl > 0 then
  return {0, 0, lock_ttl}
end
if lock_ttl == -1 then
  -- 永久锁，直接拒
  return {0, 0, -1}
end
-- 滑窗维护
redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, now - window_ms)

local max = tonumber(ARGV[2])
local peek_only = ARGV[7] == "1"

if peek_only then
  -- 只查看，不写入窗口
  local attempts = redis.call('ZCARD', KEYS[1])
  if attempts < max then
    local ttl = redis.call('PTTL', KEYS[1])
    return {1, ttl, 0}
  end
  -- 已耗尽：按超标流程直接加锁（可以累计 strikes）
  -- ↓↓↓ 下面与你原来的“超标：阶梯锁定”分支复用逻辑 ↓↓↓
else
  -- 正常失败路径：写入一次失败到窗口
  local seq = redis.call('INCR', KEYS[1]..":ctr")
  redis.call('PEXPIRE', KEYS[1]..":ctr", window_ms)
  local member = tostring(now) .. "-" .. tostring(seq)
  redis.call('ZADD', KEYS[1], now, member)
  redis.call('PEXPIRE', KEYS[1], window_ms)

  local attempts = redis.call('ZCARD', KEYS[1])
  if attempts <= max then
    local ttl = redis.call('PTTL', KEYS[1])
    return {1, ttl, 0}
  end
  -- 已耗尽：进入加锁流程
end

-- 超标：阶梯锁定（以下保持你原有实现）
local base_lock = tonumber(ARGV[3])
local escalates = ARGV[4]
local function parseEscl(s, strikes)
  if not s or s == '' then return base_lock end
  local final = base_lock
  for pair in string.gmatch(s, '([^,]+)') do
    local th, ms = string.match(pair, '(%d+):(%d+)')
    if th ~= nil and tonumber(strikes) >= tonumber(th) then
      final = tonumber(ms)
    end
  end
  return final
end

local strike_inc = ARGV[6] == "1"
local strikes = 0
if strike_inc then
  strikes = tonumber(redis.call('INCR', KEYS[3]))
  redis.call('PEXPIRE', KEYS[3], 30 * 24 * 60 * 60 * 1000)
else
  strikes = tonumber(redis.call('GET', KEYS[3]) or "0")
end

local lock_ms = parseEscl(escalates, strikes)

local ttl2 = redis.call('PTTL', KEYS[2])
if not ttl2 or ttl2 == -2 then
  redis.call('SET', KEYS[2], 1, 'PX', lock_ms)
elseif ttl2 == -1 then
  -- 永久锁不动
else
  local ok = pcall(function() redis.call('PEXPIRE', KEYS[2], lock_ms, 'GT') end)
  if not ok and ttl2 < lock_ms then
    redis.call('PEXPIRE', KEYS[2], lock_ms)
  end
end

return {0, 0, lock_ms}
