dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    kapt(libs.springBoot.configuration.processor)
}
