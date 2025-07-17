
dependencies {
    implementation(libs.springBootStarter.web)
    implementation(project(Modules.TRACE.STARTER))
    implementation(project(Modules.Context.STARTER))
    implementation(libs.kotlinxCoroutines.reactor)
    implementation(libs.kotlinxCoroutines.core)
}

