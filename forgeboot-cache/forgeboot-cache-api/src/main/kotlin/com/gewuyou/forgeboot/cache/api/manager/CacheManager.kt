package com.gewuyou.forgeboot.cache.api.manager

import com.gewuyou.forgeboot.cache.api.service.CacheService

/**
 * 缓存管理器
 *
 * 提供对不同缓存服务的统一访问接口，通过指定的命名空间获取对应的缓存服务实例。
 * 该接口定义了核心方法用于获取泛型化的缓存服务对象。
 *
 * @since 2025-06-16 22:11:06
 * @author gewuyou
 */
interface CacheManager {

    /**
     * 获取指定命名空间的缓存服务实例
     *
     * @param namespace 缓存服务的命名空间标识
     * @return 返回与命名空间关联的 CacheService 实例
     */
    fun getCache(namespace: String): CacheService

    /**
     * 清除指定命名空间下的所有缓存数据
     *
     * @param namespace 要清除缓存数据的命名空间标识
     */
    fun clear(namespace: String)

    /**
     * 清除所有命名空间下的缓存数据
     *
     * 通常用于全局缓存刷新或系统清理操作
     */
    fun clearAll()
}