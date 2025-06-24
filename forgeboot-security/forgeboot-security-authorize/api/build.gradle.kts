
dependencies {
    api(project(Modules.Security.CORE))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.security)
    kapt(libs.springBoot.configuration.processor)
}
