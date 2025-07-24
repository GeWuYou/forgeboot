plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    kapt(libs.org.pf4j)
    implementation(project(Modules.Demo.Plugin.API))
    implementation(project(Modules.Plugin.CORE))
    implementation(project(Modules.Plugin.SPRING))
    implementation(libs.springBoot.starter)
}
