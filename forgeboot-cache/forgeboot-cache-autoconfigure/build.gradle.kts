plugins {
    alias { libs.plugins.kotlin.plugin.spring }
}
dependencies {
    compileOnly(project(Modules.Cache.API))
    compileOnly(project(Modules.Cache.IMPL))
    compileOnly(libs.springBoot.autoconfigure)
    implementation(libs.springBootStarter.redis)
    implementation(libs.jackson.databind)
    implementation(libs.redisson.springBootStarter)
    api(project(Modules.Core.SERIALIZATION))
}