---
--- Created by gewuyou.
--- DateTime: 2025/9/21 12:46
---
-- idem_acquire.lua
-- KEYS[1] = key
-- ARGV[1] = ttlSeconds

--[[
  幂等性获取函数
  检查指定键是否存在，如果存在则返回0表示获取失败，
  如果不存在则创建键并设置状态为PENDING和过期时间，返回1表示获取成功

  @param key 键名
  @param ttlSeconds 过期时间（秒）
  @return 0表示键已存在获取失败，1表示键不存在创建成功
]]

if redis.call('EXISTS', KEYS[1]) == 1 then
  -- 键已存在，返回失败
  return 0
end

-- 设置键的状态为PENDING并设置过期时间
redis.call('HSET', KEYS[1], 'status', 'PENDING')
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
return 1
