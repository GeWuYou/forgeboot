
dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    compileOnly(platform(libs.springCloudDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(project(Modules.I18n.API))
    compileOnly(project(Modules.I18n.IMPL))
}
