dependencies {
    implementation(platform(libs.springBootDependencies.bom))
    api(project(Modules.Core.EXTENSION))
    api(project(Modules.Context.STARTER))
    compileOnly(project(Modules.TRACE.API))
    compileOnly(platform(libs.springCloudDependencies.bom))
    compileOnly(libs.springBootStarter.webflux)
    compileOnly(libs.springBootStarter.web)
    // Spring Cloud OpenFeign (Compile Only)
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    compileOnly(libs.springCloudStarter.openfeign)
    kapt(libs.springBoot.configuration.processor)
}
