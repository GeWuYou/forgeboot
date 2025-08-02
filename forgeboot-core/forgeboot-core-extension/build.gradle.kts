dependencies {
    val libs = rootProject.libs
    compileOnly(libs.slf4j.api)
    implementation(libs.kotlinReflect)
    implementation(libs.kotlinxCoroutines.reactor)
}