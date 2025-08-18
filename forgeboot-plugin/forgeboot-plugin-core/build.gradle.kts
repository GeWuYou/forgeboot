
dependencies {
    implementation(libs.org.yaml.snakeyaml)
    api(libs.org.pf4j)
    api(libs.org.pf4jSpring)
}
// 全局排除 slf4j-reload4j
configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-reload4j")
}