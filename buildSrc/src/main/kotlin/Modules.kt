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
     * 提供Web开发相关的功能，包括数据传输对象（DTO）、验证、版本控制及日志功能
     */
    object Webmvc {
        const val STARTER = ":forgeboot-webmvc-spring-boot-starter"
        const val DTO = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-dto"
        const val EXCEPTION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-exception-spring-boot-starter"
        const val VALIDATION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-validation"
        const val VERSION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-version-spring-boot-starter"
        const val LOGGER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-logger-spring-boot-starter"
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
        const val STARTER = ":forgeboot-banner"
        const val API = ":forgeboot-banner:forgeboot-banner-api"
        const val IMPL = ":forgeboot-banner:forgeboot-banner-impl"
        const val AUTOCONFIGURE = ":forgeboot-banner:forgeboot-banner-autoconfigure"
    }

    /**
     * Security模块：安全认证与授权模块集合
     * 包含认证(Authenticate)和授权(Authorize)两个子模块
     */
    object Security {
        private const val SECURITY = ":forgeboot-security-spring-boot-starter"
        private const val AUTHENTICATE = "${SECURITY}:forgeboot-security-authenticate-spring-boot-starter"
        private const val AUTHORIZE = "${SECURITY}:forgeboot-security-authorize-spring-boot-starter"
        const val CORE = "${SECURITY}:forgeboot-security-core"
        /**
         * Authenticate模块：身份认证支持模块
         * 提供用户身份认证相关功能，包含API、实现及自动配置模块
         */
        object Authenticate {
            const val STARTER = AUTHENTICATE
            const val API = "${AUTHENTICATE}:forgeboot-security-authenticate-api"
            const val IMPL = "${AUTHENTICATE}:forgeboot-security-authenticate-impl"
            const val AUTOCONFIGURE =
                "${AUTHENTICATE}:forgeboot-security-authenticate"
        }

        /**
         * Authorize模块：权限控制支持模块
         * 提供基于角色或策略的权限控制功能，包含API、实现及自动配置模块
         */
        object Authorize {
            const val STARTER = AUTHORIZE
            const val API = "${AUTHORIZE}:forgeboot-security-authorize-api"
            const val IMPL =  "${AUTHORIZE}:forgeboot-security-authorize-impl"
            const val AUTOCONFIGURE =
                "${AUTHORIZE}:forgeboot-security-authorize-autoconfigure"
        }
    }
    object Plugin {
        private const val PLUGIN = ":forgeboot-plugin-spring-boot-starter"
        const val CORE = "${PLUGIN}:forgeboot-plugin-core"
        const val SPRING = "${PLUGIN}:forgeboot-plugin-spring"
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