dependencies {
    implementation(project(Modules.Core.EXTENSION))
    implementation(libs.springBootStarter.aop)

    implementation(libs.kotlinxCoroutines.reactor)

    compileOnly(libs.springBootStarter.web)
}
