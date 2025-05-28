
dependencies {
    compileOnly(platform(libs.springBootDependencies.bom))
//    compileOnly(platform(libs.springCloudDependencies.bom))
//    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
}

