
dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
    compileOnly(platform(libs.springCloudDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(project(Modules.I18n.API))
    compileOnly(project(Modules.I18n.IMPL))
    implementation(project(Modules.Core.EXTENSION))
}
