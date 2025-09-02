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

package com.gewuyou.forgeboot.webmvc.dto.api

import com.gewuyou.forgeboot.webmvc.dto.api.entities.SimpleInfo

/**
 * 预定义的常用信息对象集合
 */
object Infos {
    /**
     * 请求成功信息
     */
    val OK = SimpleInfo(200, "ok", "success")

    /**
     * 错误请求信息
     */
    val BAD_REQUEST = SimpleInfo(400, "bad_request", "failure")

    /**
     * 未授权信息
     */
    val UNAUTHORIZED = SimpleInfo(401, "unauthorized", "unauthorized")

    /**
     * 资源未找到信息
     */
    val NOT_FOUND = SimpleInfo(404, "not_found", "resource is not found")

    /**
     * 服务器内部错误信息
     */
    val INTERNAL_SERVER_ERROR = SimpleInfo(500, "internal_server_error", "internal server error")
}
