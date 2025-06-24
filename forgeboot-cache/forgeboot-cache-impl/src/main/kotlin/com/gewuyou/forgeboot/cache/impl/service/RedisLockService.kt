package com.gewuyou.forgeboot.cache.impl.service

import com.gewuyou.forgeboot.cache.api.exception.CacheException
import com.gewuyou.forgeboot.cache.api.service.LockService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Redis 锁服务实现类
 *
 * 该类通过 Redisson 提供分布式锁功能，确保在分布式环境下关键代码块的线程安全执行。
 *
 * @property redissonClient Redisson 客户端实例，用于获取和操作分布式锁
 * @since 2025-06-18 21:17:21
 * @author gewuyou
 */
class RedisLockService(
    private val redissonClient: RedissonClient
) : LockService {

    /**
     * 在分布式锁保护下执行指定的操作
     *
     * 尝试获取指定 key 的分布式锁，若成功则执行 supplier 函数并确保最终释放锁；
     * 若未能在指定时间内获取锁，则抛出 CacheException 异常。
     *
     * @param key 分布式锁的唯一标识
     * @param timeout 获取锁的最大等待时间
     * @param supplier 需要在锁保护下执行的业务逻辑
     * @return supplier 执行后的返回结果
     * @throws CacheException 如果无法获取到分布式锁
     */
    override fun <T> executeWithLock(key: String, timeout: Duration, supplier: () -> T): T {
        // 获取 Redisson 分布式锁实例
        val lock: RLock = redissonClient.getLock(key)
        // 尝试在指定时间内获取锁
        val acquired: Boolean = lock.tryLock(timeout.seconds, TimeUnit.SECONDS)

        if (!acquired) {
            throw CacheException("failed to acquire distributed lock：key=$key")
        }
        return try {
            // 执行受锁保护的业务逻辑
            supplier()
        } finally {
            // 确保当前线程持有锁的情况下释放锁
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    /**
     * 使用读锁在受保护的上下文中执行指定操作
     *
     * 获取指定 key 的读锁并在限定时间内执行 supplier。如果无法获得锁则抛出异常。
     *
     * @param key 读锁的唯一标识
     * @param timeout 获取锁的最大等待时间
     * @param supplier 需要在读锁保护下执行的业务逻辑
     * @return supplier 执行后的返回结果
     * @throws CacheException 如果无法获取到读锁
     */
    override fun <T> executeWithReadLock(key: String, timeout: Duration, supplier: () -> T): T {
        val rwLock = redissonClient.getReadWriteLock(key).readLock()
        val acquired = rwLock.tryLock(timeout.seconds, TimeUnit.SECONDS)
        if (!acquired) {
            throw CacheException("failed to obtain a read lock：$key")
        }
        return try {
            supplier()
        } finally {
            if (rwLock.isHeldByCurrentThread) {
                rwLock.unlock()
            }
        }
    }

    /**
     * 使用写锁在受保护的上下文中执行指定操作
     *
     * 获取指定 key 的写锁并在限定时间内执行 supplier。如果无法获得锁则抛出异常。
     *
     * @param key 写锁的唯一标识
     * @param timeout 获取锁的最大等待时间
     * @param supplier 需要在写锁保护下执行的业务逻辑
     * @return supplier 执行后的返回结果
     * @throws CacheException 如果无法获取到写锁
     */
    override fun <T> executeWithWriteLock(key: String, timeout: Duration, supplier: () -> T): T {
        val rwLock = redissonClient.getReadWriteLock(key).writeLock()
        val acquired = rwLock.tryLock(timeout.seconds, TimeUnit.SECONDS)
        if (!acquired) {
            throw CacheException("failed to obtain a write lock：$key")
        }
        return try {
            supplier()
        } finally {
            if (rwLock.isHeldByCurrentThread) {
                rwLock.unlock()
            }
        }
    }
}