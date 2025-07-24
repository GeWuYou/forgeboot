
dependencies {
    api(libs.org.yaml.snakeyaml)
    api(libs.org.pf4j)
}
// 全局排除 slf4j-reload4j
configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-reload4j")
}