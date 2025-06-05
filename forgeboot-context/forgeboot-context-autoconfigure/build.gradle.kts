plugins{
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    compileOnly(project(Modules.Context.API))
    compileOnly(project(Modules.Context.IMPL))
    compileOnly(libs.springCloudDependencies.bom)
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(libs.springCloudStarter.openfeign)
}
