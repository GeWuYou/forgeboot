
dependencies {
    compileOnly(libs.springBootStarter.jpa)
    compileOnly(libs.org.mapstruct)
    implementation(project(Modules.Webmvc.DTO))
}
