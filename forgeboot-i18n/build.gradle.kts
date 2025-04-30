extra {
    // 需要SpringBootBom
    setProperty(ProjectFlags.USE_SPRING_BOOT_BOM, true)
    setProperty(ProjectFlags.USE_CONFIGURATION_PROCESSOR, true)
}
dependencies {
    implementation(project(Modules.Core.EXTENSION))

    compileOnly(libs.springBootStarter.web)
    // Spring Boot WebFlux
    compileOnly(libs.springBootStarter.webflux)
}
