extra {
    // 需要SpringBootBom
    setProperty(ProjectFlags.USE_SPRING_BOOT_BOM, true)
}
dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(libs.springBootStarter.aop)

    implementation(libs.kotlinxCoroutines.reactor)

    compileOnly(libs.springBootStarter.web)
}
