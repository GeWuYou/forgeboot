---
--- Created by gewuyou.
--- DateTime: 2025/9/21 13:29
---
-- ratelimit_consume.lua
-- KEYS[1] = key
-- ARGV = {capacity, refill_tokens, refill_period_ms, now_ms, requested}

--[[
    限流消费函数：基于令牌桶算法实现的 Redis Lua 脚本限流逻辑。

    参数说明：
    - KEYS[1]: Redis 中用于存储限流状态的键名
    - ARGV[1] (capacity): 令牌桶的最大容量
    - ARGV[2] (refill_tokens): 每个周期内补充的令牌数量
    - ARGV[3] (refill_period_ms): 补充令牌的时间周期（毫秒）
    - ARGV[4] (now_ms): 当前时间戳（毫秒）
    - ARGV[5] (requested): 请求消耗的令牌数量

    返回值：
    - allowed: 是否允许本次请求（1 表示允许，0 表示拒绝）
    - tokens: 剩余令牌数
    - wait_ms: 如果被拒绝，需要等待多少毫秒后重试
]]

-- 从 ARGV 中解析限流参数
-- KEYS[1] = bucket key
-- ARGV = {capacity, refill_tokens, refill_period_ms, requested}
local capacity   = tonumber(ARGV[1])
local refill     = tonumber(ARGV[2])
local period_ms  = tonumber(ARGV[3])
local requested  = tonumber(ARGV[4])

if requested < 1 then requested = 1 end
if capacity <= 0 then return {0, 0, period_ms} end

-- 统一使用 Redis 服务器时间，避免客户端漂移
local t = redis.call('TIME') -- {sec, usec}
local now_ms = t[1] * 1000 + math.floor(t[2] / 1000)

-- 默认状态：满桶
local tokens = capacity
local ts_ms  = now_ms

-- 若键存在，则读取当前令牌数和上次更新时间
if redis.call('EXISTS', KEYS[1]) == 1 then
  local data = redis.call('HMGET', KEYS[1], 'tokens', 'ts')
  tokens = tonumber(data[1]) or capacity
  ts_ms  = tonumber(data[2]) or now_ms
end

-- 根据时间差计算应补充的令牌数，并更新令牌桶
local delta = now_ms - ts_ms
if delta > 0 and refill > 0 and period_ms > 0 then
  local add = math.floor(delta * refill / period_ms)
  if add > 0 then
    tokens = math.min(capacity, tokens + add)
    ts_ms  = now_ms
  end
end

-- 判断是否允许此次请求并扣除相应令牌
local allowed = 0
if tokens >= requested then
  tokens  = tokens - requested
  allowed = 1
end

-- 将新的令牌数和时间戳写入 Redis 并设置过期时间
redis.call('HMSET', KEYS[1], 'tokens', tokens, 'ts', ts_ms)
local ttl_ms = math.max(period_ms, period_ms * 2)
redis.call('PEXPIRE', KEYS[1], ttl_ms)

-- 计算需等待的时间（若请求未被允许）
local wait_ms = 0
if allowed == 0 then
  if refill > 0 then
    local need = requested - tokens
    wait_ms = math.ceil(need * period_ms / refill)
  else
    wait_ms = period_ms
  end
end

return {allowed, tokens, wait_ms}
