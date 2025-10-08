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

package com.gewuyou.forgeboot.storage.api.entities

import java.time.Instant

/**
 * 存储桶数据类
 *
 * @property name 存储桶名称
 * @property creationDate 存储桶创建时间
 * @property region 存储桶所在区域，可为空
 * @since 2025-10-08 09:02:47
 * @author gewuyou
 */
data class StorageBucket(val name: String, val creationDate: Instant, val region: String?)
