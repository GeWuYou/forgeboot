package com.gewuyou.forgeboot.security.authenticate.api.service

import org.springframework.security.core.userdetails.UserDetails


/**
 * 用户详情服务
 *
 * @author gewuyou
 * @since 2024-11-05 19:34:50
 */
fun interface UserDetailsService {
    /**
     * 根据用户身份标识获取用户
     * @param principal 用户身份标识 通常为用户名 手机号 邮箱等
     * @return 用户详情 不能为空
     */
    fun loadUserByPrincipal(principal: Any): UserDetails
}
