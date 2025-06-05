
dependencies {
    compileOnly(project(Modules.Context.API))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
}
