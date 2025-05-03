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
        const val VERSION_STARTER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-version-spring-boot-starter"
        const val LOGGER_STARTER = ":forgeboot-webmvc-spring-boot-starter:forgeboot-webmvc-logger-spring-boot-starter"
    }
    object Core{
        const val ROOT = ":forgeboot-core"
        const val EXTENSION = ":forgeboot-core:forgeboot-core-extension"
    }
    object Common {
        const val ROOT = ":forgeboot-common"
        const val RESULT = ":forgeboot-common:forgeboot-common-result"
        const val RESULT_IMPL = ":forgeboot-common:forgeboot-common-result:forgeboot-common-result-impl"
        const val RESULT_API = ":forgeboot-common:forgeboot-common-result:forgeboot-common-result-api"
    }
}