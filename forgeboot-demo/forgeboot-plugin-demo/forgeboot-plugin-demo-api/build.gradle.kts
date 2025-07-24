plugins {
    alias(libs.plugins.kotlin.kapt)
}
dependencies {
    kapt(libs.org.pf4j)
    implementation(libs.org.pf4j)
}
