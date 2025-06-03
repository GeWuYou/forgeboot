
dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    compileOnly(project(Modules.Banner.IMPL))
    compileOnly(project(Modules.Banner.API))
    implementation(project(Modules.Core.EXTENSION))
}
