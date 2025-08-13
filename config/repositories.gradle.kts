// This file is used to define the repositories used by the project.
repositories {
    mavenLocal()
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
        content {
            excludeModule("io.ktor", "ktor-client-mock")
            excludeModule("io.ktor", "ktor-client-mock-jvm") // 如果你之前用的是 jvm 变体，也一并排除
        }
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
