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

package com.gewuyou.forgeboot.webmvc.exception.impl


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.dto.api.entities.Failure
import com.gewuyou.forgeboot.webmvc.dto.api.entities.SimpleInfo
import com.gewuyou.forgeboot.webmvc.dto.impl.Responses
import com.gewuyou.forgeboot.webmvc.exception.api.PromptException
import com.gewuyou.forgeboot.webmvc.exception.api.config.ExceptionAdviceProperties
import com.gewuyou.forgeboot.webmvc.exception.api.enums.ExceptionLogPolicy
import com.gewuyou.forgeboot.webmvc.exception.api.hook.OtherExceptionHook
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理类
 *
 * 该类用于统一处理整个应用中抛出的各种异常，以提供统一的错误响应格式
 * 它通过使用@RestControllerAdvice注解来标识这是一个全局异常处理类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:22:18
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionAdvice(
    private val props: ExceptionAdviceProperties,
    private val hooks: List<OtherExceptionHook>,
) {
    /**
     * 异常处理器
     *
     * 用于处理除特定异常外的所有其他异常
     *
     * @param e 异常
     * @param request HTTP请求对象
     * @return 响应
     * @since 2024/4/13 上午12:29
     */
    @ExceptionHandler(Exception::class)
    fun handleOtherException(e: Exception, request: HttpServletRequest): Failure {
        // 1) 链式 SPI：使用 Spring 的注解/接口顺序机制来排序 hooks
        val sortedHooks = hooks.toMutableList()
        AnnotationAwareOrderComparator.sort(sortedHooks)

        for (hook in sortedHooks) {
            val out = hook.handle(e, request)
            if (out != null) return out
        }

        // 2) 默认逻辑
        log.error("other exception:", e)
        return Responses.fail(
            info = SimpleInfo(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                props.fallbackMessage
            )
        )
    }
    /**
     * 全局异常处理器
     *
     * 用于处理全局异常（PromptException），返回错误代码和国际化消息代码
     *
     * @param e 异常
     * @return 返回的结果
     * @since 2024/4/13 下午1:56
     */
    @ExceptionHandler(PromptException::class)
    fun handlePromptException(e: PromptException): Failure {
        when (e.logPolicy) {
            ExceptionLogPolicy.NONE -> Unit

            ExceptionLogPolicy.BRIEF_LOCATION -> {
                e.businessStack()?.let {
                    log.warn(
                        "[{}] code:{} message:{} Business stack:\n\t{}",
                        e::class.simpleName,
                        e.info.code,
                        e.info.defaultMessage,
                        it
                    )
                }
            }

            ExceptionLogPolicy.FULL_STACK -> {
                log.error(
                    "[{}] {}",
                    e::class.simpleName,
                    e.info.code,
                    e
                )
            }
        }
        return e.toFailure()
    }
}
