plugins {
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(platform(libs.springBootDependencies.bom))
    implementation(libs.springBoot.autoconfigure)
    implementation(project(Modules.Context.STARTER))
    compileOnly(platform(libs.springCloudDependencies.bom))
    compileOnly(libs.springBootStarter.web)
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(project(Modules.TRACE.API))
    compileOnly(project(Modules.TRACE.IMPL))
    // Spring Cloud OpenFeign (Compile Only)
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    compileOnly(libs.springCloudStarter.openfeign)
    // Reactor Core (Compile Only)
    // https://mvnrepository.com/artifact/io.projectreactor/reactor-core
    compileOnly(libs.reactor.core)
}
