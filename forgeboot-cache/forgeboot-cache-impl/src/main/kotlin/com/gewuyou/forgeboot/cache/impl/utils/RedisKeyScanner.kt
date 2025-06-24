package com.gewuyou.forgeboot.cache.impl.utils

import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions
import java.nio.charset.StandardCharsets

/**
 *Redis 键扫描器
 *
 * @since 2025-06-17 21:32:15
 * @author gewuyou
 */
class RedisKeyScanner(
    private val redisConnectionFactory: RedisConnectionFactory
) {

    /**
     * 扫描指定 pattern 下的所有 key，使用非阻塞 SCAN 命令
     *
     * @param pattern 通配符，如 "user:*"
     * @param count 每次扫描的数量 默认1000
     * @return 匹配到的所有 key 列表
     */
    fun scan(pattern: String, count: Long = 1000): Set<String> {
        require(pattern.isNotBlank()) { "Scan pattern must not be blank" }
        val keys = mutableSetOf<String>()
        redisConnectionFactory.connection.use { connection ->
            val options = ScanOptions.scanOptions().match(pattern).count(count).build()
            connection.keyCommands().scan(options).use { cursor ->
                while (cursor.hasNext()) {
                    val rawKey = cursor.next()
                    keys.add(String(rawKey, StandardCharsets.UTF_8))
                }
            }
        }
        return keys
    }
}