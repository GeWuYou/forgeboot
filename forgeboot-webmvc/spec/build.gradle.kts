
dependencies {
    compileOnly(libs.springBootStarter.jpa)
    implementation(project(Modules.Webmvc.DTO))
    implementation(project(Modules.Core.EXTENSION))
}
