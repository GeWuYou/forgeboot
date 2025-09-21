/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.safeguard.redis.support

import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.StringRedisTemplate
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/**
 * Lua 脚本执行器，用于在 Redis 中加载和执行 Lua 脚本。
 *
 * @property redis 用于与 Redis 进行交互的 StringRedisTemplate 实例
 * @since 2025-09-21 13:05:40
 * @author gewuyou
 */
class LuaScriptExecutor(
    private val redis: StringRedisTemplate,
) {
    /**
     * 表示一个 Lua 脚本的封装类。
     *
     * @property path 脚本在 classpath 下的路径
     * @property sha 脚本的 SHA1 值（用于 EVALSHA）
     * @property bytes 脚本的内容字节数组
     */
    private data class Script(val path: String, val sha: String, val bytes: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Script) return false

            if (path != other.path) return false
            if (sha != other.sha) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = path.hashCode()
            result = 31 * result + sha.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    /** 按路径缓存已加载的脚本 */
    private val byPath = ConcurrentHashMap<String, Script>()

    /** 按 SHA1 缓存已加载的脚本 */
    private val bySha = ConcurrentHashMap<String, Script>()

    /**
     * 预加载指定路径的 Lua 脚本到 Redis。
     * 优先使用 Redis 返回的 SHA1，若失败则使用本地计算的 SHA1。
     *
     * @param classpathPath Lua 脚本在 classpath 中的路径
     * @return 加载后脚本对应的 SHA1 值
     */
    fun load(classpathPath: String): String {
        val bytes = ClassPathResource(classpathPath).inputStream.use { it.readBytes() }
        val shaFromRedis = redis.execute { it.scriptingCommands().scriptLoad(bytes) }
        val sha = shaFromRedis ?: sha1Hex(bytes)
        val script = Script(classpathPath, sha, bytes)
        byPath[classpathPath] = script
        bySha[sha] = script
        return sha
    }

    /**
     * 使用 EVALSHA 执行 Lua 脚本并返回 MULTI 类型结果。
     *
     * @param sha 脚本的 SHA1 值
     * @param keys Redis 键列表
     * @param args 脚本参数列表
     * @return 执行结果（MULTI 类型）
     */
    fun evalSha(sha: String, keys: List<String>, args: List<Any>): Any? =
        eval(sha, ReturnType.MULTI, keys, args)

    /**
     * 使用 EVALSHA 执行 Lua 脚本并返回 INTEGER 类型结果，并转换为 Long。
     *
     * @param sha 脚本的 SHA1 值
     * @param keys Redis 键列表
     * @param args 脚本参数列表
     * @return 执行结果（Long 类型）
     * @throws IllegalStateException 当返回值类型不支持转换时抛出异常
     */
    fun evalShaToLong(sha: String, keys: List<String>, args: List<Any>): Long {
        return when (val res = eval(sha, ReturnType.INTEGER, keys, args)) {
            is Number -> res.toLong()
            is String -> res.toLong()
            is ByteArray -> String(res).toLong()
            else -> error("Unexpected Lua INTEGER return: ${res?.javaClass}")
        }
    }

    /**
     * 通用方法：根据指定的 ReturnType 执行 Lua 脚本。
     * 若 EVALSHA 失败（如 Redis 重启导致脚本缓存丢失），自动回退为 EVAL 文本方式执行。
     *
     * @param sha 脚本的 SHA1 值
     * @param returnType 指定返回值类型
     * @param keys Redis 键列表
     * @param args 脚本参数列表
     * @return 执行结果
     * @throws IllegalStateException 当脚本未加载时抛出异常
     */
    fun eval(
        sha: String,
        returnType: ReturnType,
        keys: List<String>,
        args: List<Any>,
    ): Any? {
        val script = bySha[sha] ?: error("Lua script not loaded: $sha")
        val keyBytes = keys.map { it.toByteArray(StandardCharsets.UTF_8) }.toTypedArray()
        val argBytes = args.map { a ->
            a as? ByteArray ?: a.toString().toByteArray(StandardCharsets.UTF_8)
        }.toTypedArray()
        val argv = keyBytes + argBytes

        return redis.execute { conn ->
            try {
                conn.scriptingCommands().evalSha(
                    sha.toByteArray(StandardCharsets.UTF_8),
                    returnType,
                    keys.size,
                    *argv
                )
            } catch (_: Exception) {
                // Redis 重启或脚本缓存丢失 → 回退到 EVAL 文本
                conn.scriptingCommands().eval(
                    script.bytes,
                    returnType,
                    keys.size,
                    *argv
                )
            }
        }
    }

    /**
     * 删除 Redis 中指定的键。
     *
     * @param key 要删除的键名
     */
    fun del(key: String) {
        redis.delete(key)
    }

    /**
     * 使用 SHA-1 算法对字节数组进行哈希处理，返回十六进制字符串。
     *
     * @param bytes 输入的字节数组
     * @return SHA-1 哈希值的十六进制字符串表示
     */
    private fun sha1Hex(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-1")
        return md.digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
