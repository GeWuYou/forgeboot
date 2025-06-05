dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    kapt(libs.springBoot.configuration.processor)
    compileOnly(libs.springBootStarter.web)
}
