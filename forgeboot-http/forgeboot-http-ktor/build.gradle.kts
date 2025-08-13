dependencies {
    api(libs.io.ktor.clientCore)
    implementation(libs.io.ktor.clientContentNegotiation)
    implementation(libs.io.ktor.serializationKotlinxJson)
    implementation(libs.io.ktor.clientLogging)

    // test
    testImplementation(libs.org.junit.jupiter.api)
    testImplementation(libs.io.ktor.clientCio)
    testImplementation(libs.io.ktor.clientMock)
    testImplementation(libs.kotlinxCorountinesTest)
    testRuntimeOnly(libs.org.junit.jupiter.engine)
    testRuntimeOnly(libs.org.junit.platform)
}
