/*
 *
 *  *
 *  *  * Copyright (c) 2025 GeWuYou
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *  *
 *
 */

package com.gewuyou.forgeboot.webmvc.exception.autoconfigure

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.exception.api.config.ValidationExceptionProperties
import com.gewuyou.forgeboot.webmvc.exception.impl.ValidationExceptionAdvice
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

/**
 * 验证异常自动配置
 *
 * 该配置类负责在满足特定条件时自动配置验证异常处理器
 * 当类路径下存在验证相关的异常类且配置启用时，会自动注册验证异常处理Advice
 *
 * @since 2025-12-30 12:13:59
 * @author gewuyou
 */
@AutoConfiguration
@ConditionalOnClass(
    name = [
        "org.springframework.web.bind.MethodArgumentNotValidException",
        "jakarta.validation.ConstraintViolationException"
    ]
)
@ConditionalOnProperty(
    prefix = "forgeboot.webmvc.exception.validation",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class ValidationExceptionAutoConfiguration {

    /**
     * 创建验证异常处理Advice的Bean
     *
     * 该方法在容器中不存在ValidationExceptionAdvice类型的Bean时创建一个新的实例
     * 并记录初始化日志信息
     *
     * @return ValidationExceptionAdvice 验证异常处理Advice实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun validationExceptionAdvice(
        props: ValidationExceptionProperties
    ): ValidationExceptionAdvice {
        // 记录验证异常处理初始化日志
        log.info("初始化验证异常处理")
        return ValidationExceptionAdvice(props)
    }
}
