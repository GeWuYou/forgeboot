// The settings file is the entry point of every Gradle build.
// Its primary purpose is to define the subprojects.
// It is also used for some aspects of project-wide configuration, like managing plugins, dependencies, etc.
// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Include the `app` and `utils` subprojects in the build.
// If there are changes in only one of the projects, Gradle will rebuild only the one that has changed.
// Learn more about structuring projects with Gradle - https://docs.gradle.org/8.7/userguide/multi_project_builds.html


rootProject.name = "forgeboot"

//region module webmvc
include(
    "forgeboot-webmvc",
    ":forgeboot-webmvc:version",
    ":forgeboot-webmvc:logger",
    ":forgeboot-webmvc:exception",
    ":forgeboot-webmvc:dto",
    ":forgeboot-webmvc:validation"
)
project(":forgeboot-webmvc").name = "forgeboot-webmvc-spring-boot-starter"
project(":forgeboot-webmvc:version").name = "forgeboot-webmvc-version-spring-boot-starter"
project(":forgeboot-webmvc:logger").name = "forgeboot-webmvc-logger-spring-boot-starter"
project(":forgeboot-webmvc:exception").name = "forgeboot-webmvc-exception-spring-boot-starter"
project(":forgeboot-webmvc:dto").name = "forgeboot-webmvc-dto"
project(":forgeboot-webmvc:validation").name = "forgeboot-validation"
//endregion

//region module core
include(
    "forgeboot-core",
    ":forgeboot-core:forgeboot-core-extension"
)
project(":forgeboot-core").name = "forgeboot-core"
project(":forgeboot-core:forgeboot-core-extension").name = "forgeboot-core-extension"
//endregion

//region module i18n
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

//region module webflux
//include(
//    "forgeboot-webflux",
//)
//project(":forgeboot-webflux").name = "forgeboot-webflux-spring-boot-starter"
//endregion

//region module trace
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