plugins {
    // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
    // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
    // and are applied in the project's `build.gradle.kts` files as required.
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Add a dependency on the Kotlin Gradle plugin, so that convention plugins can apply it.
    implementation(libs.kotlinGradlePlugin)
}
gradlePlugin {
    plugins {
        register("forgeboot-i18n-key-gen") {
            id = "i18n-key-gen"
            implementationClass = "I18nKeyGenPlugin"
            description =
                "提供一个用于生成 i18n key文件 的插件"
        }
    }
}
