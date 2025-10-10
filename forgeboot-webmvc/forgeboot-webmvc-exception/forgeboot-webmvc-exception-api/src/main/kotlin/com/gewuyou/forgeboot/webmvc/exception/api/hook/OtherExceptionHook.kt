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

package com.gewuyou.forgeboot.webmvc.exception.api.hook

import com.gewuyou.forgeboot.webmvc.dto.api.entities.Failure
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered

/**
 * 其他异常钩子
 *
 * @since 2025-09-23 21:05:04
 * @author gewuyou
 */
interface OtherExceptionHook : Ordered {
    /**
     * 处理异常
     *
     * @param e 异常对象
     * @param request HTTP请求对象，可能为null
     * @return 处理结果，如果处理成功则返回Failure对象，否则返回null
     */
    fun handle(e: Exception, request: HttpServletRequest?): Failure?

    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
}
