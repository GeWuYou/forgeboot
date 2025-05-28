
dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
    compileOnly(platform(libs.springCloudDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(project(Modules.I18N.API))
    compileOnly(project(Modules.I18N.IMPL))
    implementation(project(Modules.Core.EXTENSION))
}
