// This file is used to define the repositories used by the project.
repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
    maven {
        url = uri("https://raw.githubusercontent.com/eurotech/kura_addons/mvn-repo/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/spring/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/spring-plugin/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/gradle-plugin/")
    }
    mavenCentral()
    google()
    gradlePluginPortal()
}
