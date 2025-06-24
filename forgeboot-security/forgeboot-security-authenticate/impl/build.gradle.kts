plugins{
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    api(project(Modules.Security.Authenticate.API))
    implementation(project(Modules.Security.CORE))
    implementation(project(Modules.Core.EXTENSION))
    implementation(project(Modules.Webmvc.DTO))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.security)
}
