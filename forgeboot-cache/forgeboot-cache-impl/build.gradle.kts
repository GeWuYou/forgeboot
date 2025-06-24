plugins{
    alias(libs.plugins.kotlin.plugin.spring)
}
dependencies {
    compileOnly(project(Modules.Cache.API))
    compileOnly(project(Modules.Core.SERIALIZATION))
    implementation(libs.springBootStarter.aop)
    implementation(libs.springBootStarter.redis)
    implementation(libs.com.github.benManes.caffeine)
    implementation(libs.kotlinxCoroutines.core)
    implementation(libs.jackson.databind)
    implementation(libs.redisson.springBootStarter)
}
