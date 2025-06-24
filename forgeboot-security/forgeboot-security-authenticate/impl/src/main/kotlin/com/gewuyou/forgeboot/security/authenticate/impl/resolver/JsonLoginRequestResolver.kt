package com.gewuyou.forgeboot.security.authenticate.impl.resolver

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.registry.LoginRequestTypeRegistry
import com.gewuyou.forgeboot.security.authenticate.api.resolver.LoginRequestResolver
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.AuthenticationRequest
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType

/**
 * JSON登录请求解析器
 *
 * 该解析器用于处理JSON格式的登录请求数据，通过两次反序列化过程来支持动态类型的登录请求。
 * 首先读取请求中的type字段以确定具体的登录类型，然后根据该类型再次反序列化为对应的对象。
 *
 * @property objectMapper 用于JSON序列化和反序列化的Jackson ObjectMapper实例
 * @property loginRequestTypeRegistry 登录请求类型的注册表，用于根据登录类型获取实际类
 *
 * @since 2025-06-10 16:37:22
 * @author gewuyou
 */
class JsonLoginRequestResolver(
    private val objectMapper: ObjectMapper,
    private val loginRequestTypeRegistry: LoginRequestTypeRegistry, // 动态注册哪些类型
) : LoginRequestResolver {

    /**
     * 判断是否支持给定的内容类型
     *
     * 该方法检查内容类型字符串中是否包含JSON媒体类型（application/json），
     * 表示当前解析器可以处理该类型的内容。
     *
     * @param contentType 请求的内容类型字符串
     * @return 如果支持返回true，否则返回false
     */
    override fun supports(contentType: String): Boolean {
        return contentType.contains(MediaType.APPLICATION_JSON_VALUE)
    }

    /**
     * 解析登录请求
     *
     * 从HttpServletRequest中读取JSON输入流，并进行两次反序列化操作：
     * 1. 首次反序列化为AuthenticationRequest以提取type字段；
     * 2. 根据type字段从注册表中查找对应的实际类；
     * 3. 再次反序列化为实际类的对象。
     *
     * @param request HTTP请求对象
     * @return 解析后的登录请求对象
     * @throws IllegalArgumentException 如果不支持的登录类型
     */
    override fun resolve(request: HttpServletRequest): LoginRequest {
        val json = request.inputStream.reader().readText()
        // 1. 先反序列化为 AuthenticationRequest，读取 type 字段
        val tempRequest = objectMapper.readValue(json, AuthenticationRequest::class.java)
        val loginType = tempRequest.type
        // 2. 从注册表中获取真正对应的类型
        val clazz = loginRequestTypeRegistry.getTypeForLoginType(loginType)
            ?: throw IllegalArgumentException("Unsupported login type: $loginType")
        // 3. 再次反序列化为真正请求体类型
        return objectMapper.readValue(json, clazz)
    }
}