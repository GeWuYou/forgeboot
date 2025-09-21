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

package com.gewuyou.forgeboot.safeguard.core.enums

/**
 * 幂等性状态枚举类
 * 用于定义操作的幂等性状态，确保操作的唯一性和一致性
 *
 * @since 2025-09-21 10:56:27
 * @author gewuyou
 */
enum class IdempotencyStatus {
    /**
     * 待处理状态
     * 表示操作正在等待处理或尚未开始处理
     */
    PENDING,

    /**
     * 成功状态
     * 表示操作已成功完成
     */
    SUCCESS
}
