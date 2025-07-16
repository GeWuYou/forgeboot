dependencies {
    compileOnly(project(Modules.Context.API))
    compileOnly(project(Modules.Core.SERIALIZATION))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    implementation(libs.jackson.databind)
    implementation(libs.kotlinxCoroutines.reactor)
}
