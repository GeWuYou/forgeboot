plugins {
    // 基本 Java 支持
    alias(libs.plugins.java)
    alias(libs.plugins.javaLibrary)
    // Maven 发布支持
    alias(libs.plugins.maven.publish)
    // 语义版本插件
    alias(libs.plugins.axionRelease)
    // Kotlin Spring 支持
    alias(libs.plugins.kotlin.plugin.spring)
}

// 配置 SCM 版本插件
scmVersion {
    tag {
        prefix.set("")  // 不加 v，生成 1.0.1 而不是 v1.0.1
    }
    versionIncrementer("incrementMinorIfNotOnRelease")
    hooks {
        pre(
            "fileUpdate", mapOf(
                "file" to "README.md",
                "pattern" to "version: (.*)",
                "replacement" to "version: \$version"
            )
        )
        pre("commit")
        post("push")
    }
}

// 设置项目版本
version = scmVersion.version

// 配置目录路径
val configDir = "$rootDir/config/"
val tasksDir = "$configDir/tasks/"

// 全局项目配置
allprojects {
    // 设置全局属性
    ext {
        set(ProjectFlags.IS_ROOT_MODULE, false)
    }
    afterEvaluate {
        if (project.getPropertyByBoolean(ProjectFlags.IS_ROOT_MODULE)) {
            /**
             * 由于 Kotlin 插件引入时会自动添加依赖，但根项目不需要 Kotlin 依赖，因此需要排除 Kotlin 依赖
             */
            configurations.implementation {
                exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
            }
        }
    }
    project.group = "com.gewuyou.forgeboot"
}

// 子项目配置
subprojects {
    version = rootProject.version
    afterEvaluate {
        val isRootModule = project.getPropertyByBoolean(ProjectFlags.IS_ROOT_MODULE)
        val isStarterModule = project.name.contains("starter")
        val parentProject = project.parent
        // 让父项目引入子项目
        parentProject?.dependencies?.add("api", project(project.path))
        if (isStarterModule && !isRootModule) {
            // Starter 模块依赖配置
            dependencies {
                implementation(platform(libs.springBootDependencies.bom))
                implementation(platform(libs.springCloudDependencies.bom))
                annotationProcessor(libs.springBoot.configuration.processor)
            }
        }
    }
    // 应用插件和配置
    val libs = rootProject.libs
    apply {
        plugin(libs.plugins.java.get().pluginId)
        plugin(libs.plugins.javaLibrary.get().pluginId)
        plugin(libs.plugins.maven.publish.get().pluginId)
        plugin(libs.plugins.kotlin.jvm.get().pluginId)
        plugin(libs.plugins.axionRelease.get().pluginId)
        // 导入仓库配置
        from(file("$configDir/repositories.gradle.kts"))
        // 导入源代码任务
        from(file("$tasksDir/sourceTask.gradle.kts"))
    }

    // 发布配置
    publishing {
        repositories {
            // 本地仓库
            maven {
                name = "localRepo"
                url = uri("file://D:/Maven/mvn_repository")
            }
            // GitHub Packages 仓库
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/GeWuYou/forgeboot")
                credentials {
                    username = System.getenv("GITHUB_USERNAME")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
            // Gitea 仓库
            val host = System.getenv("GITEA_HOST")
            host?.let {
                maven {
                    isAllowInsecureProtocol = true
                    name = "Gitea"
                    url = uri("http://${it}/api/packages/gewuyou/maven")
                    credentials(HttpHeaderCredentials::class.java) {
                        name = "Authorization"
                        value = "token ${System.getenv("GITEA_TOKEN")}"
                    }
                    authentication {
                        create("header", HttpHeaderAuthentication::class.java)
                    }
                }
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                val projectName = project.name
                from(components["java"])
                groupId = project.group.toString()
                artifactId = projectName
                version = project.version.toString()

                pom {
                    name.set(projectName)
                    description.set("Part of Forgeboot Starters")
                    url.set("https://github.com/GeWuYou/forgeboot")

                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("gewuyou")
                            name.set("gewuyou")
                            email.set("gewuyou1024@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/GeWuYou/forgeboot.git")
                        developerConnection.set("scm:git:ssh://github.com/GeWuYou/forgeboot.git")
                        url.set("https://github.com/GeWuYou/forgeboot")
                    }
                }
            }
        }
    }

    // 依赖配置
    dependencies {

    }

    // Java 插件扩展配置
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    // 任务配置
    tasks.withType<Jar> {
        isEnabled = true
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    tasks.named<Test>("test") {
        useJUnitPlatform()
    }
}

// 获取项目布尔属性的辅助函数
fun Project.getPropertyByBoolean(key: String): Boolean {
    return properties[key]?.toString()?.toBoolean() ?: false
}
