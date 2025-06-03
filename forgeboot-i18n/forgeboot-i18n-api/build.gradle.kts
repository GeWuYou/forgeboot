
dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
    compileOnly(libs.springBootStarter.webflux)
    kapt(libs.springBoot.configuration.processor)
}

