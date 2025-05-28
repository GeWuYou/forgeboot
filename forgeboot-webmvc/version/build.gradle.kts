dependencies {
    implementation(project(Modules.Core.EXTENSION))
    kapt(libs.springBoot.configuration.processor)
    compileOnly(libs.springBootStarter.web)
}
