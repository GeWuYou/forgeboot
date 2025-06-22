plugins{
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    api(project(Modules.Webmvc.DTO))
    api(project(Modules.TRACE.STARTER))
    compileOnly(libs.springBootStarter.validation)
    compileOnly(libs.springBootStarter.web)
    kapt(libs.springBoot.configuration.processor)
}
