plugins {
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(libs.springBootStarter.aop)
    implementation(libs.springExpression)
    implementation(project(Modules.Core.EXTENSION))
    implementation(project(Modules.Cache.STARTER))
    implementation(project(Modules.Webmvc.DTO))    // webflux
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(project(Modules.Security.Authorize.API))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.security)
}
