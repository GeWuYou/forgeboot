plugins{
    alias(libs.plugins.forgeboot.i18n.keygen)
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    implementation(project(Modules.Core.EXTENSION))
    api(project(Modules.I18n.STARTER))
    api(project(Modules.TRACE.STARTER))
    implementation(project(Modules.Webmvc.DTO))
    compileOnly(libs.springBootStarter.validation)
    compileOnly(libs.springBootStarter.web)
    kapt(libs.springBoot.configuration.processor)
}
i18nKeyGen {
    rootPackage.set("com.gewuyou.forgeboot.webmvc.extension.i18n")
    readPath.set("src/main/resources/i18n/${project.name}")
    level.set(3)
}
