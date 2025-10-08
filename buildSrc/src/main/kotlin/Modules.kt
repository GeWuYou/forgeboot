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

/**
 * Modules对象用于统一管理项目中的各个模块路径
 * 主要作用是提供一个集中定义和访问模块路径的地方，以便在项目中保持一致性和可维护性
 *
 * @since 2025-04-03 09:07:33
 * @author gewuyou
 */
object Modules {

    /**
     * Context模块：Spring Boot Starter上下文支持模块
     * 提供基础的上下文功能，包含API、实现及自动配置模块
     */
    object Context {
        const val STARTER = ":forgeboot-context-spring-boot-starter"
        const val API = ":forgeboot-context-spring-boot-starter:forgeboot-context-api"
        const val IMPL = ":forgeboot-context-spring-boot-starter:forgeboot-context-impl"
        const val AUTOCONFIGURE = ":forgeboot-context-spring-boot-starter:forgeboot-context-autoconfigure"
    }

    /**
     * Webmvc模块：Spring Boot Starter对WebMVC的支持模块
     * 提供Web开发相关的功能，包括数据传输对象（DTO2）、验证、版本控制及日志功能
     */
    object Webmvc {
        object DTO {
            const val STARTER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-dto-spring-boot-starter"
            const val API = "${STARTER}:forgeboot-webmvc-dto-api"
            const val IMPL = "${STARTER}:forgeboot-webmvc-dto-impl"
            const val AUTOCONFIGURE = "${STARTER}:forgeboot-webmvc-dto-autoconfigure"
        }

        object EXCEPTION {
            const val STARTER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-exception-spring-boot-starter"
            const val API = "${STARTER}:forgeboot-webmvc-exception-api"
            const val IMPL = "${STARTER}:forgeboot-webmvc-exception-impl"
            const val AUTOCONFIGURE = "${STARTER}:forgeboot-webmvc-exception-autoconfigure"
        }
        const val STARTER = ":forgeboot-webmvc-spring-boot-starter"
        const val VALIDATION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-validation"
        const val VERSION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-version-spring-boot-starter"

        object LOGGER {
            const val STARTER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-logger-spring-boot-starter"
            const val API = "${STARTER}:forgeboot-webmvc-logger-api"
            const val IMPL = "${STARTER}:forgeboot-webmvc-logger-impl"
            const val AUTOCONFIGURE = "${STARTER}:forgeboot-webmvc-logger-autoconfigure"
        }
        object Spec{
            const val CORE = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-spec-core"
            const val JPA = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-spec-jpa"
        }
    }

    /**
     * Core模块：项目的基础核心模块
     * 包含项目的通用核心功能以及扩展支持
     */
    object Core {
        const val ROOT = ":forgeboot-core"
        const val EXTENSION = ":forgeboot-core:forgeboot-core-extension"
        const val SERIALIZATION = ":forgeboot-core:forgeboot-core-serialization"
    }

    /**
     * Cache模块：缓存支持模块
     * 提供缓存功能，包含API、实现及自动配置模块
     */
    object Cache {
        const val STARTER = ":forgeboot-cache-spring-boot-starter"
        const val API = ":forgeboot-cache-spring-boot-starter:forgeboot-cache-api"
        const val IMPL = ":forgeboot-cache-spring-boot-starter:forgeboot-cache-impl"
        const val AUTOCONFIGURE = ":forgeboot-cache-spring-boot-starter:forgeboot-cache-autoconfigure"
    }
    /**
     * I18n模块：国际化支持模块
     * 提供多语言支持功能，包含API、实现及自动配置模块
     */
    object I18n {
        const val STARTER = ":forgeboot-i18n-spring-boot-starter"
        const val API = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-api"
        const val IMPL = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-impl"
        const val AUTOCONFIGURE = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-autoconfigure"
    }

    /**
     * TRACE模块：分布式链路追踪支持模块
     * 提供请求链路追踪能力，包含API、实现及自动配置模块
     */
    object TRACE {
        const val STARTER = ":forgeboot-trace-spring-boot-starter"
        const val API = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-api"
        const val IMPL = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-impl"
        const val AUTOCONFIGURE = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-autoconfigure"
    }

    /**
     * Banner模块：启动横幅定制模块
     * 负责自定义应用启动时显示的横幅信息
     */
    object Banner {
        const val ALL = ":forgeboot-banner"
        const val API = ":forgeboot-banner:forgeboot-banner-api"
        const val IMPL = ":forgeboot-banner:forgeboot-banner-impl"
        const val AUTOCONFIGURE = ":forgeboot-banner:forgeboot-banner-autoconfigure"
    }

    object Plugin {
        const val STARTER = ":forgeboot-plugin-spring-boot-starter"
        const val CORE = "${STARTER}:forgeboot-plugin-core"
        const val SPRING = "${STARTER}:forgeboot-plugin-spring"
    }

    object Safeguard {
        const val STARTER = ":forgeboot-safeguard-spring-boot-starter"
        const val CORE = "${STARTER}:forgeboot-safeguard-core"
        const val REDIS = "${STARTER}:forgeboot-safeguard-redis"
        const val AUTOCONFIGURE = "${STARTER}:forgeboot-safeguard-autoconfigure"
    }
    object Storage {
        const val STARTER = ":forgeboot-storage-spring-boot-starter"
        const val API = ":forgeboot-storage-spring-boot-starter:forgeboot-storage-api"
        const val IMPL = ":forgeboot-storage-spring-boot-starter:forgeboot-storage-impl"
        const val AUTOCONFIGURE = ":forgeboot-storage-spring-boot-starter:forgeboot-storage-autoconfigure"
    }
    object Demo{
        private const val ROOT = ":forgeboot-demo"
        object Plugin{
            private const val PLUGIN = "${ROOT}:forgeboot-plugin-demo"
            const val API = "${PLUGIN}:forgeboot-plugin-demo-api"
            const val IMPL = "${PLUGIN}:forgeboot-plugin-demo-impl"
            const val SERVER = "${PLUGIN}:forgeboot-plugin-demo-server"
        }

    }
}