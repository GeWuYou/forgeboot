/**
 * This settings.gradle.kts file configures the Gradle build for the forgeboot project.
 * It sets up dependency resolution, plugins, and includes all relevant subprojects.
 */

// Configures the dependency resolution management across all subprojects
dependencyResolutionManagement {
    /**
     * Use Maven Central as the default repository (where Gradle will download dependencies)
     * The @Suppress annotation is used to bypass warnings about unstable API usage.
     */
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

// Applies necessary plugins for the build process
plugins {
    /**
     * Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
     * This ensures consistent Java versions across different environments.
     */
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Sets the root project name
rootProject.name = "forgeboot"

//region module context
/**
 * Includes and configures projects related to 'forgeboot-context'
 * This module appears to be focused on contextual functionality within the application.
 */
include(
    "forgeboot-context",
    ":forgeboot-context:forgeboot-context-api",
    ":forgeboot-context:forgeboot-context-impl",
    ":forgeboot-context:forgeboot-context-autoconfigure",
)
project(":forgeboot-context").name = "forgeboot-context-spring-boot-starter"
project(":forgeboot-context:forgeboot-context-api").name = "forgeboot-context-api"
project(":forgeboot-context:forgeboot-context-impl").name = "forgeboot-context-impl"
project(":forgeboot-context:forgeboot-context-autoconfigure").name = "forgeboot-context-autoconfigure"
//endregion

//region module banner
/**
 * Includes and configures projects related to 'forgeboot-banner'
 * This module likely deals with banners or startup messages in the application.
 */
include(
    "forgeboot-banner",
    ":forgeboot-banner:forgeboot-banner-api",
    ":forgeboot-banner:forgeboot-banner-impl",
    ":forgeboot-banner:forgeboot-banner-launcher",
)
project(":forgeboot-banner").name = "forgeboot-banner"
project(":forgeboot-banner:forgeboot-banner-api").name = "forgeboot-banner-api"
project(":forgeboot-banner:forgeboot-banner-impl").name = "forgeboot-banner-impl"
project(":forgeboot-banner:forgeboot-banner-launcher").name = "forgeboot-banner-launcher"
//endregion

//region module webmvc
/**
 * Includes and configures projects related to 'forgeboot-webmvc'
 * This module seems to handle Spring WebMVC-related functionalities like logging,
 * exceptions, DTO handling, validation, etc.
 */
include(
    "forgeboot-webmvc",
    ":forgeboot-webmvc:version",
    ":forgeboot-webmvc:logger",
    ":forgeboot-webmvc:exception",
    ":forgeboot-webmvc:exception-i18n",
    ":forgeboot-webmvc:dto",
    ":forgeboot-webmvc:validation",
    ":forgeboot-webmvc:spec-core",
    ":forgeboot-webmvc:spec-jpa",
)
project(":forgeboot-webmvc").name = "forgeboot-webmvc-spring-boot-starter"
project(":forgeboot-webmvc:version").name = "forgeboot-webmvc-version-spring-boot-starter"
project(":forgeboot-webmvc:logger").name = "forgeboot-webmvc-logger-spring-boot-starter"
project(":forgeboot-webmvc:exception").name = "forgeboot-webmvc-exception-spring-boot-starter"
project(":forgeboot-webmvc:exception-i18n").name = "forgeboot-webmvc-exception-i18n-spring-boot-starter"
project(":forgeboot-webmvc:dto").name = "forgeboot-webmvc-dto"
project(":forgeboot-webmvc:validation").name = "forgeboot-webmvc-validation"
project(":forgeboot-webmvc:spec-core").name = "forgeboot-webmvc-spec-core"
project(":forgeboot-webmvc:spec-jpa").name = "forgeboot-webmvc-spec-jpa"
//endregion

//region module core
/**
 * Includes and configures projects related to 'forgeboot-core'
 * This module represents foundational components of the application.
 */
include(
    "forgeboot-core",
    ":forgeboot-core:forgeboot-core-extension",
    ":forgeboot-core:forgeboot-core-serialization",
)
project(":forgeboot-core").name = "forgeboot-core"
project(":forgeboot-core:forgeboot-core-extension").name = "forgeboot-core-extension"
project(":forgeboot-core:forgeboot-core-serialization").name = "forgeboot-core-serialization"
//endregion

//region module i18n
/**
 * Includes and configures projects related to 'forgeboot-i18n'
 * This module handles internationalization (i18n) support.
 */
include(
    "forgeboot-i18n",
    ":forgeboot-i18n:forgeboot-i18n-api",
    ":forgeboot-i18n:forgeboot-i18n-impl",
    ":forgeboot-i18n:forgeboot-i18n-autoconfigure"
)
project(":forgeboot-i18n").name = "forgeboot-i18n-spring-boot-starter"
project(":forgeboot-i18n:forgeboot-i18n-api").name = "forgeboot-i18n-api"
project(":forgeboot-i18n:forgeboot-i18n-impl").name = "forgeboot-i18n-impl"
project(":forgeboot-i18n:forgeboot-i18n-autoconfigure").name = "forgeboot-i18n-autoconfigure"
//endregion

//region module trace
/**
 * Includes and configures projects related to 'forgeboot-trace'
 * This module handles distributed tracing functionality.
 */
include(
    "forgeboot-trace",
    ":forgeboot-trace:forgeboot-trace-api",
    ":forgeboot-trace:forgeboot-trace-impl",
    ":forgeboot-trace:forgeboot-trace-autoconfigure",
)
project(":forgeboot-trace").name = "forgeboot-trace-spring-boot-starter"
project(":forgeboot-trace:forgeboot-trace-api").name = "forgeboot-trace-api"
project(":forgeboot-trace:forgeboot-trace-impl").name = "forgeboot-trace-impl"
project(":forgeboot-trace:forgeboot-trace-autoconfigure").name = "forgeboot-trace-autoconfigure"
//endregion

//region module trace
/**
 * Includes and configures projects related to 'forgeboot-trace'
 * This module handles distributed tracing functionality.
 */
include(
    "forgeboot-demo",
    ":forgeboot-demo:forgeboot-trace-demo",
    ":forgeboot-demo:forgeboot-plugin-demo",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-api",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-impl",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-server",
)
//endregion
include(
    "forgeboot-plugin",
    ":forgeboot-plugin:forgeboot-plugin-core",
    ":forgeboot-plugin:forgeboot-plugin-spring",
)
project(":forgeboot-plugin").name = "forgeboot-plugin-spring-boot-starter"
project(":forgeboot-plugin:forgeboot-plugin-core").name = "forgeboot-plugin-core"
project(":forgeboot-plugin:forgeboot-plugin-spring").name = "forgeboot-plugin-spring"
//region module  cache
include(
    "forgeboot-cache",
    ":forgeboot-cache:forgeboot-cache-api",
    ":forgeboot-cache:forgeboot-cache-impl",
    ":forgeboot-cache:forgeboot-cache-autoconfigure"
    )
project(":forgeboot-cache").name = "forgeboot-cache-spring-boot-starter"
project(":forgeboot-cache:forgeboot-cache-api").name = "forgeboot-cache-api"
project(":forgeboot-cache:forgeboot-cache-impl").name = "forgeboot-cache-impl"
project(":forgeboot-cache:forgeboot-cache-autoconfigure").name = "forgeboot-cache-autoconfigure"
//endregion
