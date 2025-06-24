package com.gewuyou.forgeboot.security.authenticate.api.service

import org.springframework.security.core.userdetails.UserDetails


/**
 *用户缓存服务
 *
 * @since 2025-02-15 17:15:17
 * @author gewuyou
 */
interface UserCacheService {
    /**
     * 从缓存中获取用户信息
     * @param principal 用户标识
     * @return 用户信息 返回null表示缓存中没有该用户信息或信息已过期
     */
    fun getUserFromCache(principal: String): UserDetails?

    /**
     * 将用户信息缓存到缓存中 注意，请将过期时间设置得尽可能短，以防止缓存与数据库出现数据不一致
     * @param userDetails 用户信息
     */
    fun putUserToCache(userDetails: UserDetails)

    /**
     * 从缓存中移除用户信息
     * @param principal 用户标识
     */
    fun removeUserFromCache(principal: String)
}