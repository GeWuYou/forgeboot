plugins{
    alias(libs.plugins.forgeboot.i18n.keygen)
}
dependencies {
    api(project(Modules.I18n.API))
    api(project(Modules.TRACE.API))
    implementation(libs.kotlinReflect)
    compileOnly(libs.springBootDependencies.bom)
    compileOnly(libs.jackson.annotations)
    compileOnly(libs.springBootStarter.jpa)
    compileOnly(libs.springBootStarter.validation)
}
i18nKeyGen {
    rootPackage.set("com.gewuyou.forgeboot.webmvc.dto.i18n")
    level.set(3)
    readPath.set("src/main/resources/i18n/${project.name}")
}
