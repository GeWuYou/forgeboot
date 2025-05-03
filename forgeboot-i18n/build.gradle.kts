dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(project(Modules.Common.RESULT_API))

    compileOnly(libs.springBootStarter.web)
    // Spring Boot WebFlux
    compileOnly(libs.springBootStarter.webflux)
}
