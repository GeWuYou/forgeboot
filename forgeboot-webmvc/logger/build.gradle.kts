dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(libs.springBootStarter.aop)
    implementation(libs.kotlinxCoroutines.reactor)
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    compileOnly(libs.springBootStarter.web)
}
