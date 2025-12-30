/*
 *
 *  *
 *  *  * Copyright (c) 2025 GeWuYou
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *  *
 *
 */

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
 * exceptions, DTO2 handling, validation, etc.
 */
include(
    "forgeboot-webmvc",
    ":forgeboot-webmvc:version",
    ":forgeboot-webmvc:forgeboot-webmvc-logger",
    ":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-api",
    ":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-impl",
    ":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-autoconfigure",
    ":forgeboot-webmvc:forgeboot-webmvc-exception",
    ":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-api",
    ":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-impl",
    ":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-autoconfigure",
    ":forgeboot-webmvc:forgeboot-webmvc-dto",
    ":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-api",
    ":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-impl",
    ":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-autoconfigure",
    ":forgeboot-webmvc:validation",
    ":forgeboot-webmvc:spec-core",
    ":forgeboot-webmvc:spec-jpa",
    ":forgeboot-webmvc:spec-jimmer",
)
project(":forgeboot-webmvc").name = "forgeboot-webmvc-spring-boot-starter"
project(":forgeboot-webmvc:version").name = "forgeboot-webmvc-version-spring-boot-starter"
project(":forgeboot-webmvc:forgeboot-webmvc-logger").name = "forgeboot-webmvc-logger-spring-boot-starter"
project(":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-api").name = "forgeboot-webmvc-logger-api"
project(":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-impl").name = "forgeboot-webmvc-logger-impl"
project(":forgeboot-webmvc:forgeboot-webmvc-logger:forgeboot-webmvc-logger-autoconfigure").name =
    "forgeboot-webmvc-logger-autoconfigure"
project(":forgeboot-webmvc:forgeboot-webmvc-exception").name = "forgeboot-webmvc-exception-spring-boot-starter"
project(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-api").name =
    "forgeboot-webmvc-exception-api"
project(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-impl").name =
    "forgeboot-webmvc-exception-impl"
project(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-autoconfigure").name =
    "forgeboot-webmvc-exception-autoconfigure"
project(":forgeboot-webmvc:forgeboot-webmvc-dto").name = "forgeboot-webmvc-dto-spring-boot-starter"
project(":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-api").name = "forgeboot-webmvc-dto-api"
project(":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-impl").name = "forgeboot-webmvc-dto-impl"
project(":forgeboot-webmvc:forgeboot-webmvc-dto:forgeboot-webmvc-dto-autoconfigure").name =
    "forgeboot-webmvc-dto-autoconfigure"
project(":forgeboot-webmvc:validation").name = "forgeboot-webmvc-validation"
project(":forgeboot-webmvc:spec-core").name = "forgeboot-webmvc-spec-core"
project(":forgeboot-webmvc:spec-jpa").name = "forgeboot-webmvc-spec-jpa"
project(":forgeboot-webmvc:spec-jimmer").name = "forgeboot-webmvc-spec-jimmer"
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

//region module demo
/**
 * Includes and configures projects related to 'forgeboot-trace'
 * This module handles distributed tracing functionality.
 */
include(
    "forgeboot-demo",
    ":forgeboot-demo:forgeboot-trace-demo",
    ":forgeboot-demo:forgeboot-plugin-demo",
    ":forgeboot-demo:forgeboot-safeguard-demo",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-api",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-impl",
    ":forgeboot-demo:forgeboot-plugin-demo:forgeboot-plugin-demo-server",
)
//endregion

//region module plugin
include(
    "forgeboot-plugin",
    ":forgeboot-plugin:forgeboot-plugin-core",
    ":forgeboot-plugin:forgeboot-plugin-autoconfigure",
)
project(":forgeboot-plugin").name = "forgeboot-plugin-spring-boot-starter"
project(":forgeboot-plugin:forgeboot-plugin-core").name = "forgeboot-plugin-core"
project(":forgeboot-plugin:forgeboot-plugin-autoconfigure").name = "forgeboot-plugin-autoconfigure"
//endregion

//region module cache
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

//region module context
/**
 * Includes and configures projects related to 'forgeboot-context'
 * This module appears to be focused on contextual functionality within the application.
 */
include(
    "forgeboot-safeguard",
    ":forgeboot-safeguard:forgeboot-safeguard-core",
    ":forgeboot-safeguard:forgeboot-safeguard-redis",
    ":forgeboot-safeguard:forgeboot-safeguard-autoconfigure",
)
project(":forgeboot-safeguard").name = "forgeboot-safeguard-spring-boot-starter"
project(":forgeboot-safeguard:forgeboot-safeguard-core").name = "forgeboot-safeguard-core"
project(":forgeboot-safeguard:forgeboot-safeguard-redis").name = "forgeboot-safeguard-redis"
project(":forgeboot-safeguard:forgeboot-safeguard-autoconfigure").name = "forgeboot-safeguard-autoconfigure"
//endregion

//region module storage
/**
 * Includes and configures projects related to storage module in the multi-project build.
 * This function sets up the storage module and its subprojects including API, implementation,
 * and auto-configuration modules, along with a Spring Boot starter module.
 */
include(
    "forgeboot-storage",
    ":forgeboot-storage:forgeboot-storage-api",
    ":forgeboot-storage:forgeboot-storage-impl",
    ":forgeboot-storage:forgeboot-storage-autoconfigure",
)

// Configure project names for the storage module and its subprojects
project(":forgeboot-storage").name = "forgeboot-storage-spring-boot-starter"
project(":forgeboot-storage:forgeboot-storage-api").name = "forgeboot-storage-api"
project(":forgeboot-storage:forgeboot-storage-impl").name = "forgeboot-storage-impl"
project(":forgeboot-storage:forgeboot-storage-autoconfigure").name = "forgeboot-storage-autoconfigure"
//endregion


// >>> Gradle Buddy: On-Demand Modules (DO NOT EDIT THIS BLOCK) >>>
// Generated at: 2025-12-30T12:39:33.537959900
// Loaded: 4, Excluded: 0, Total: 4
include(":forgeboot-demo:forgeboot-trace-demo")
include(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-api")
include(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-autoconfigure")
include(":forgeboot-webmvc:forgeboot-webmvc-exception:forgeboot-webmvc-exception-impl")
// <<< Gradle Buddy: End Of Block <<<
