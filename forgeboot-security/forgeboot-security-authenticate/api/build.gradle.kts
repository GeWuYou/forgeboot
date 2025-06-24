
dependencies {
    api(project(Modules.Webmvc.DTO))
    compileOnly(project(Modules.Security.CORE))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.security)
    kapt(libs.springBoot.configuration.processor)
}
