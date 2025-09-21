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

package com.gewuyou.forgeboot.safeguard.core.model

/**
 * 冷却票据数据类
 * 用于表示某个操作是否已获得执行权限的票据状态
 *
 * @property acquired 表示是否已获得票据，true表示已获得，false表示未获得
 * @since 2025-09-21 10:55:33
 * @author gewuyou
 */
data class CooldownTicket(
    val acquired: Boolean,
)
