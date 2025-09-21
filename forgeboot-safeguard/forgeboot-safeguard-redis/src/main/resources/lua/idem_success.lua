---
--- Created by gewuyou.
--- DateTime: 2025/9/21 12:49
---
-- idem_success.lua
-- KEYS[1] = key
-- ARGV[1] = ttlSeconds
-- ARGV[2] = payloadType (may be empty)
-- ARGV[3] = payload bytes (as Redis bulk string)

--[[
  将指定键的状态从PENDING更新为SUCCESS，并设置相关负载数据和过期时间

  @param KEYS[1] string 键名
  @param ARGV[1] string 过期时间(秒)
  @param ARGV[2] string 负载数据类型(可选)
  @param ARGV[3] string 负载数据内容(可选)
  @return number 操作结果: 1表示更新成功, 0表示键不存在或状态不是PENDING
]]
if redis.call('EXISTS', KEYS[1]) == 0 then
  return 0
end

-- 检查当前状态是否为PENDING
local st = redis.call('HGET', KEYS[1], 'status')
if st ~= 'PENDING' then
  return 0
end

-- 更新状态为SUCCESS并设置负载数据
redis.call('HSET', KEYS[1], 'status', 'SUCCESS')
if ARGV[2] and string.len(ARGV[2]) > 0 then
  redis.call('HSET', KEYS[1], 'type', ARGV[2])
end
if ARGV[3] and string.len(ARGV[3]) > 0 then
  redis.call('HSET', KEYS[1], 'body', ARGV[3])
end

-- 设置过期时间
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
return 1
