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

package com.gewuyou.forgeboot.webmvc.exception.autoconfigure

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.exception.api.config.ExceptionAdviceProperties
import com.gewuyou.forgeboot.webmvc.exception.api.hook.OtherExceptionHook
import com.gewuyou.forgeboot.webmvc.exception.impl.GlobalExceptionAdvice
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

/**
 * 异常自动配置
 *
 * @since 2025-05-13 11:48:01
 * @author gewuyou
 */
@AutoConfiguration
@EnableConfigurationProperties(ExceptionAdviceProperties::class)
@ConditionalOnProperty(
    prefix = "forgeboot.webmvc.exception",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class ExceptionAutoConfiguration {
    /**
     * 创建异常处理器Bean
     * 当容器中不存在ExceptionHandler类型的Bean时，创建并注册一个默认的异常处理器
     *
     * @return ExceptionHandler 异常处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun exceptionHandler(props: ExceptionAdviceProperties, hooks: List<OtherExceptionHook>): GlobalExceptionAdvice {
        // 初始化异常处理组件
        log.info("初始化异常处理")
        return GlobalExceptionAdvice(props, hooks)
    }
}
