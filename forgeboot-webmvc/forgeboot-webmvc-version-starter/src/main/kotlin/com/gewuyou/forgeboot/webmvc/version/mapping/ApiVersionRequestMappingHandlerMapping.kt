package com.gewuyou.forgeboot.webmvc.version.mapping


import com.gewuyou.forgeboot.webmvc.version.annotation.ApiVersion
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

/**
 *API 版本请求映射处理程序映射
 *
 * @since 2025-02-04 20:30:44
 * @author gewuyou
 */
class ApiVersionRequestMappingHandlerMapping : RequestMappingHandlerMapping() {
    /**
     * 判断是否处理特定类型的Bean
     * 仅处理标注了 @RestController 注解的类
     * @param beanType 要判断的Bean类型
     * @return 如果类型标注了 @RestController，则返回true，否则返回false
     */
    override fun isHandler(beanType: Class<*>): Boolean {
        // 仅处理标注了 @RestController 注解的类
        return AnnotatedElementUtils.hasAnnotation(beanType, RestController::class.java)
    }

    /**
     * 获取方法的映射信息
     * 首先尝试从方法上获取 @ApiVersion 注解，如果不存在，则尝试从类上获取
     * 如果存在 @ApiVersion 注解，则会根据注解中的版本信息来组合新地映射路径
     * @param method 方法对象
     * @param handlerType 处理器类型
     * @return 可能包含版本信息的 RequestMappingInfo，如果不存在 @ApiVersion 注解，则返回原始的映射信息
     */
    override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        // 获取原始的 @RequestMapping 信息
        val requestMappingInfo = super.getMappingForMethod(method, handlerType) ?: return null
        // 优先获取方法上的注解
        val methodApiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion::class.java)
        // 当方法注解存在时构建并返回 RequestMappingInfo
        methodApiVersion?.let {
            return combineVersionMappings(requestMappingInfo, it.value)
        }
        // 获取类上的 @ApiVersion 注解
        val classApiVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion::class.java)
        classApiVersion?.let {
            return combineVersionMappings(requestMappingInfo, it.value)
        }
        // 当类注解不存在时返回原始的 RequestMappingInfo
        return requestMappingInfo
    }

    /**
     * 组合版本路径，支持多个版本
     * @param originalMapping 原始的 RequestMappingInfo
     * @param versions 版本数组
     * @return 组合后的 RequestMappingInfo
     */
    private fun combineVersionMappings(
        originalMapping: RequestMappingInfo,
        versions: Array<out String>
    ): RequestMappingInfo {
        return versions
            .map {
                // 加上版本前缀
                RequestMappingInfo
                    .paths("/api/$it")
                    .build()
            }.reduce {
                // 组合
                    acc, mapping ->
                acc.combine(mapping)
            }
            // 组合原始 RequestMapping
            .combine(originalMapping)
    }
}