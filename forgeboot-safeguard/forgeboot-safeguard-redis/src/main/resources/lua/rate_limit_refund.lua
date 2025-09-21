---
--- Created by gewuyou.
--- DateTime: 2025/9/21 13:48
---
-- KEYS[1] = bucket key
-- ARGV = {capacity, requested}
local capacity  = tonumber(ARGV[1])
local requested = tonumber(ARGV[2])
if requested < 1 then requested = 1 end

-- 当前状态（默认满桶）
local tokens = capacity
local ts_ms  = 0

if redis.call('EXISTS', KEYS[1]) == 1 then
  local data = redis.call('HMGET', KEYS[1], 'tokens', 'ts')
  tokens = tonumber(data[1]) or capacity
  ts_ms  = tonumber(data[2]) or 0
end

-- 回滚：加回请求的许可，但不超过容量
tokens = math.min(capacity, tokens + requested)

-- 写回（注意：不更新 ts，避免“加回后立刻再按 now 补桶”而过度给票）
redis.call('HMSET', KEYS[1], 'tokens', tokens, 'ts', ts_ms)

-- 安全 TTL，避免键泄漏（与 consume 的 TTL 策略一致）
-- 这里用 2 个补桶周期（无害且简单）
-- （如果你想更严格，可把 period 也作为 ARGV 传入，这里保持简化）
return tokens
