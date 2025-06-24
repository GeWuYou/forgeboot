package com.gewuyou.forgeboot.cache.api.service

import java.time.Duration

/**
 * 锁服务接口，用于在分布式环境中执行需要加锁的操作。
 *
 * @since 2025-06-16 22:11:57
 * @author gewuyou
 */
interface LockService {

    /**
     * 在锁的保护下执行指定操作。
     *
     * 使用给定的 key 获取锁，并在指定的超时时间内执行 supplier 提供的操作。
     * 如果无法在超时时间内获取锁，则可能抛出异常或返回默认值，具体取决于实现方式。
     *
     * @param <T> 返回值类型
     * @param key 锁的唯一标识符，用于区分不同的资源锁
     * @param timeout 获取锁的最大等待时间，单位为秒
     * @param supplier 需要在锁保护下执行的操作，提供返回值
     * @return 执行 supplier 后返回的结果
     */
    fun <T> executeWithLock(key: String, timeout: Duration, supplier: () -> T): T
    /**
     * 执行带读锁的操作。
     *
     * @param key 锁键
     * @param timeout 获取锁超时时间
     * @param supplier 要执行的读操作
     */
    fun <T> executeWithReadLock(key: String, timeout: Duration, supplier: () -> T): T

    /**
     * 执行带写锁的操作。
     *
     * @param key 锁键
     * @param timeout 获取锁超时时间
     * @param supplier 要执行的写操作
     */
    fun <T> executeWithWriteLock(key: String, timeout: Duration, supplier: () -> T): T
}
