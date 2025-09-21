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

package com.gewuyou.forgeboot.safeguard.core.policy

import com.gewuyou.forgeboot.safeguard.core.enums.IdemMode
import java.time.Duration

/**
 * 幂等性策略配置类
 *
 * 用于定义接口幂等性控制的策略配置，包括超时时间和处理模式
 *
 * @property ttl 幂等性记录的存活时间，超过该时间后记录将被清除
 * @property mode 幂等性处理模式，默认为返回已保存的结果
 * @since 2025-09-21 09:57:03
 * @author gewuyou
 */
data class IdempotencyPolicy(
    val ttl: Duration,
    val mode: IdemMode = IdemMode.RETURN_SAVED,
)
