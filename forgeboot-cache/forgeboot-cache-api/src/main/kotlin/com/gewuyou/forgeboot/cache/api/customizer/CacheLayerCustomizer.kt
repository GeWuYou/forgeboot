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

package com.gewuyou.forgeboot.cache.api.customizer

import com.gewuyou.forgeboot.cache.api.entities.CacheLayer

/**
 *缓存层定制器
 *
 * @since 2025-10-17 11:41:53
 * @author gewuyou
 */
fun interface CacheLayerCustomizer {
    fun customize(layers: List<CacheLayer>): List<CacheLayer>
}