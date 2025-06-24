plugins {
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    compileOnly(libs.springBootStarter.security)
    compileOnly(libs.springBootStarter.web)
    compileOnly(project(Modules.Security.Authenticate.IMPL))
    implementation(project(Modules.Security.CORE))
}
