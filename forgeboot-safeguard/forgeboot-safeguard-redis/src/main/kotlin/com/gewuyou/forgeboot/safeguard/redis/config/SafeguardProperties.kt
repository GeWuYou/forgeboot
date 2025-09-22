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

package com.gewuyou.forgeboot.safeguard.redis.config


import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * SafeguardProperties 配置类用于定义系统安全防护相关的配置属性。
 * 该类通过 @ConfigurationProperties 注解绑定 application.yml 中以 "forgeboot.safeguard" 为前缀的配置项。
 *
 * @property enabled 控制安全防护功能是否启用，默认为 true
 * @property idempotencyWaitMax 幂等性处理中 WAIT_UNTIL_DONE 模式下的最大等待时间，默认为 5 秒
 * @property rateLimiterEngine 限流器使用的引擎类型，可选 "lua" 或 "bucket4j"，默认为 "lua"
 * @since 2025-09-21 14:08:59
 * @author gewuyou
 */
@ConfigurationProperties("forgeboot.safeguard")
data class SafeguardProperties(
    var enabled: Boolean = true,

    var idempotencyWaitMax: Duration = Duration.ofSeconds(5),

    var rateLimiterEngine: String = "lua",

    var attemptLimiterEngine: String = "lua",

    var redisKeyTemplatePrefix: String = "safeguard:key_tpl",
)
