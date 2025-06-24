package com.gewuyou.forgeboot.cache.api.service

/**
 * 缓存预热服务
 * 用于在系统启动或特定时机提前加载常用缓存数据，提升后续请求的访问效率
 *
 * @since 2025-06-16 22:18:29
 * @author gewuyou
 */
fun interface CacheWarmUpService {
    /**
     * 执行缓存预热操作
     * 该方法应包含具体的缓存加载逻辑，例如查询数据库或调用其他服务获取数据并写入缓存
     */
    fun warmUp()
}
