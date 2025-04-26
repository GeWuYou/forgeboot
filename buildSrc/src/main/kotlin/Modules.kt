/**
 * Modules对象用于统一管理项目中的各个模块路径
 * 主要作用是提供一个集中定义和访问模块路径的地方，以便在项目中保持一致性和可维护性
 *
 * @since 2025-04-03 09:07:33
 * @author gewuyou
 */
object Modules {

    object Webmvc {
        const val STARTER = ":forgeboot-webmvc"
        const val VERSION_STARTER = ":forgeboot-webmvc:forgeboot-webmvc-version-starter"
        const val LOGGER_STARTER = ":forgeboot-webmvc:forgeboot-webmvc-logger-starter"
    }
}