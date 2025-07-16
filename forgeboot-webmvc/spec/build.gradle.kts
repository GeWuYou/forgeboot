
dependencies {
    compileOnly(libs.springBootStarter.jpa)
    api(project(Modules.Webmvc.DTO))
    api(project(Modules.Core.EXTENSION))
}
