dependencies {
    compileOnly(libs.springBoot.starter)
    implementation(project(Modules.Plugin.CORE))
    implementation(project(Modules.Core.EXTENSION))
}
// 全局排除 slf4j-reload4j
configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-reload4j")
}