dependencies {
    implementation(project(Modules.Core.EXTENSION))
    // Spring Cloud OpenFeign (Compile Only)
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    compileOnly(libs.springCloudStarter.openfeign)
    // Reactor Core (Compile Only)
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-core
    compileOnly(libs.reactor.core)

    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
}
