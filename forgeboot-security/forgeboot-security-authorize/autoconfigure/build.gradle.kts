plugins {
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    compileOnly(libs.springBootStarter.web)
    // webflux
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(libs.springBootStarter.security)
    compileOnly(project(Modules.Security.Authorize.IMPL))
    compileOnly(project(Modules.Security.Authorize.API))
    implementation(project(Modules.Security.CORE))
}
