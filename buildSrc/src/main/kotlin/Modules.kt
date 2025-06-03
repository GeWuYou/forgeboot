/**
 * Modules对象用于统一管理项目中的各个模块路径
 * 主要作用是提供一个集中定义和访问模块路径的地方，以便在项目中保持一致性和可维护性
 *
 * @since 2025-04-03 09:07:33
 * @author gewuyou
 */
object Modules {

    object Webmvc {
        const val STARTER = ":forgeboot-webmvc-spring-boot-starter"
        const val DTO = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-dto"
        const val VALIDATION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-validation"
        const val VERSION = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-version-spring-boot-starter"
        const val LOGGER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-logger-spring-boot-starter"
    }
    object Core{
        const val ROOT = ":forgeboot-core"
        const val EXTENSION = ":forgeboot-core:forgeboot-core-extension"
    }
    object I18n {
        const val STARTER = ":forgeboot-i18n-spring-boot-starter"
        const val API = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-api"
        const val IMPL = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-impl"
        const val AUTOCONFIGURE = ":forgeboot-i18n-spring-boot-starter:forgeboot-i18n-autoconfigure"
    }
    object TRACE{
        const val STARTER = ":forgeboot-trace-spring-boot-starter"
        const val API = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-api"
        const val IMPL = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-impl"
        const val AUTOCONFIGURE = ":forgeboot-trace-spring-boot-starter:forgeboot-trace-autoconfigure"
    }
    object  Banner {
        const val STARTER = ":forgeboot-banner"
        const val API = ":forgeboot-banner:forgeboot-banner-api"
        const val IMPL = ":forgeboot-banner:forgeboot-banner-impl"
        const val AUTOCONFIGURE = ":forgeboot-banner:forgeboot-banner-autoconfigure"
    }
}