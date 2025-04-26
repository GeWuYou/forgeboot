plugins {
    // 基本 Java 支持
    alias(libs.plugins.java)
    // Maven 发布支持
    alias(libs.plugins.maven.publish)
    // 语义版本插件
    alias(libs.plugins.axionRelease)
    // Kotlin Spring 支持
    alias(libs.plugins.kotlin.plugin.spring)
}
//scmVersion {
//    tag {
//        prefix.set("")  // 不加 v，生成 1.0.1 而不是 v1.0.1
//    }
//    versionIncrementer("incrementMinorIfNotOnRelease")
//    hooks {
//        pre("fileUpdate", mapOf(
//            "file" to "README.md",
//            "pattern" to "version: (.*)",
//            "replacement" to "version: \$version"
//        ))
//        pre("commit")
//        post("push")
//    }
//
//}
//version = scmVersion.version


val configDir = "$rootDir/config/"
val tasksDir = "$configDir/tasks/"
//apply {
//    from(file("$tasksDir/gradleTask.gradle"))
//

allprojects {
    // 设置全局属性
    ext {
        set(ProjectFlags.IS_ROOT_MODULE, false)
    }
    afterEvaluate {
        if(project.getPropertyByBoolean(ProjectFlags.IS_ROOT_MODULE)){
            /**
             * 由于 Kotlin 插件引入时会自动添加依赖，但根项目不需要 Kotlin 依赖，因此需要排除 Kotlin 依赖
             */
            configurations.implementation {
                exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
            }
        }
    }
}
subprojects {
//    afterEvaluate {
//        if (project.getPropertyByBoolean(ProjectFlags.USE_GRPC)) {
//            dependencies{
//                implementation(platform(libs.grpc.bom))
//                // gRPC Stub
//                implementation(libs.grpc.stub)
//            }
//        }
//        if (project.getPropertyByBoolean(ProjectFlags.USE_SPRING_BOOT)){
//            dependencies{
//                implementation(platform(libs.springBootDependencies.bom))
//                compileOnly(libs.springBootStarter.web)
//            }
//        }
//    }
    val libs = rootProject.libs
    apply {
        plugin(libs.plugins.java.get().pluginId)
        plugin(libs.plugins.maven.publish.get().pluginId)
        plugin(libs.plugins.kotlin.jvm.get().pluginId)
//        plugin(libs.plugins.spring.dependency.management.get().pluginId)
        // 导入仓库配置
        from(file("$configDir/repositories.gradle.kts"))
        // 导入源代码任务
        from(file("$tasksDir/sourceTask.gradle"))
//        // 导入发布配置
//        from(file("$configDir/publishing.gradle"))
    }
    dependencies {

    }
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
//    kotlin {
//        compilerOptions {
//            freeCompilerArgs.addAll("-Xjsr305=strict")
//        }
//    }
    tasks.withType<Jar> {
        isEnabled = true
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}

fun Project.getPropertyByBoolean(key: String): Boolean {
    return properties[key]?.toString()?.toBoolean() ?: false
}
