dependencies {
    api(project(Modules.Webmvc.DTO))
    api(project(Modules.Webmvc.Spec.CORE))
    compileOnly(libs.springBootStarter.jpa)
}
