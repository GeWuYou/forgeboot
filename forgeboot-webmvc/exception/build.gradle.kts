plugins{
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(project(Modules.Core.EXTENSION))
    api(project(Modules.TRACE.STARTER))
    implementation(project(Modules.Webmvc.DTO))
    compileOnly(libs.springBootStarter.validation)
    compileOnly(libs.springBootStarter.web)
    kapt(libs.springBoot.configuration.processor)
}
