// This file is used to define the repositories used by the project.
repositories {
    mavenLocal()
    val host = System.getenv("GITEA_HOST")
//    host?.let {
//        maven{
//            url = uri("${host}/api/packages/gewuyou/maven")
//        }
//    }
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
