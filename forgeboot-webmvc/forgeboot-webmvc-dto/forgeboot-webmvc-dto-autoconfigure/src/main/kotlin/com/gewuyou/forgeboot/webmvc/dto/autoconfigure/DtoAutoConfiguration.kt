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

package com.gewuyou.forgeboot.webmvc.dto.autoconfigure

import com.gewuyou.forgeboot.i18n.api.InfoResolver
import com.gewuyou.forgeboot.i18n.api.entities.ResolvedInfo
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.api.config.ResponseProps
import com.gewuyou.forgeboot.webmvc.dto.api.entities.ApiResponses
import com.gewuyou.forgeboot.webmvc.dto.api.entities.ExtraContributor
import com.gewuyou.forgeboot.webmvc.dto.impl.DefaultApiResponses
import com.gewuyou.forgeboot.webmvc.dto.impl.Responses
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 *DTO自动配置
 *
 * @since 2025-09-02 13:21:38
 * @author gewuyou
 */
@AutoConfiguration
@EnableConfigurationProperties(ResponseProps::class)
class DtoAutoConfiguration {
    /**
     * 创建默认的请求ID提供者Bean
     *
     * 当Spring容器中不存在RequestIdProvider类型的Bean时，创建一个默认的RequestIdProvider实例
     * 该默认实现始终返回"null"字符串作为请求ID
     *
     * @return RequestIdProvider 默认的请求ID提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(RequestIdProvider::class)
    fun defaultRequestIdProvider(): RequestIdProvider = RequestIdProvider { "null" }

    /**
     * 创建默认的信息解析器Bean
     *
     * 当Spring容器中不存在InfoResolver类型的Bean时，创建一个默认的InfoResolver实例
     * 该默认实现直接返回InfoLike对象中的code和defaultMessage，不进行国际化处理
     *
     * @return InfoResolver 默认的信息解析器实例
     */
    @Bean
    @ConditionalOnMissingBean(InfoResolver::class)
    fun defaultInfoResolver(): InfoResolver = InfoResolver { info ->
        /**
         * 解析InfoLike对象为ResolvedInfo对象
         *
         * 根据InfoLike对象中的messageKey获取对应的本地化消息文本，
         * 如果提供了参数args，则会将其用于消息文本中的占位符替换
         *
         * @param info 包含状态码和消息键的信息对象
         * @return ResolvedInfo 包含状态码和解析后消息文本的对象
         */
        ResolvedInfo(
            info.code,
            info.defaultMessage
        )
    }

    /**
     * 创建API响应处理Bean
     *
     * 当Spring容器中不存在ApiResponses类型的Bean时，创建一个DefaultApiResponses实例
     * 用于统一处理API响应格式，包含信息解析、请求ID生成和额外信息贡献等功能
     *
     * @param infoResolver 信息解析器，用于解析状态码和消息
     * @param requestIdProvider 请求ID提供者，用于生成请求唯一标识
     * @param contributors 额外信息贡献者列表，可为null
     * @param props 响应配置属性
     * @return ApiResponses API响应处理器实例
     */
    @Bean
    @ConditionalOnMissingBean(ApiResponses::class)
    fun apiResponses(
        infoResolver: InfoResolver,
        requestIdProvider: RequestIdProvider,
        contributors: List<ExtraContributor>?,
        props: ResponseProps,
    ): ApiResponses = DefaultApiResponses(
        infoResolver, requestIdProvider, contributors ?: emptyList(), props
    )

    /**
     * 创建并配置API响应桥接器Bean
     *
     * @param apiResponses API响应配置对象，用于初始化全局响应处理
     * @return 包含初始化逻辑的匿名对象
     */
    @Bean
    fun responsesBridge(apiResponses: ApiResponses) = object {
        /**
         * 初始化API响应处理器
         * 在Bean创建完成后自动调用，将API响应配置注入到全局响应管理器中
         */
        @PostConstruct
        fun init() {
            Responses.init(apiResponses)
        }
    }

}