dependencies {
    implementation(project(Modules.Core.EXTENSION))

    compileOnly(libs.springBootStarter.web)
    // Spring Boot WebFlux
    compileOnly(libs.springBootStarter.webflux)
}
