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
    // Kotlin kapt 支持
    alias(libs.plugins.kotlin.kapt)
    id(libs.plugins.kotlin.jvm.get().pluginId)
//    alias(libs.plugins.gradleMavenPublishPlugin)
}


// 配置 SCM 版本插件
scmVersion {
    tag {
        prefix.set("")  // 不加 v，生成 1.0.1 而不是 v1.0.1
    }
    versionIncrementer("incrementPatch")
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
    project.group = "io.github.gewuyou"
}

// 子项目配置
subprojects {
    afterEvaluate {
        val isRootModule = project.getPropertyByBoolean(ProjectFlags.IS_ROOT_MODULE)
        val parentProject = project.parent
        // 让父项目引入子项目
        parentProject?.dependencies?.add("api", project(project.path))
        if (!isRootModule) {
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
    plugins.withId(libs.plugins.java.get().pluginId) {
        extensions.configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }
    }
    plugins.withId(libs.plugins.forgeboot.i18n.keygen.get().pluginId) {
        tasks.named("kotlinSourcesJar") {
            dependsOn("generateI18nKeys")
        }
        tasks.named<Jar>("sourcesJar") {
            dependsOn("generateI18nKeys")
        }
    }

    version = rootProject.version

    apply {
//        plugin(libs.plugins.java.get().pluginId)
//        plugin(libs.plugins.javaLibrary.get().pluginId)
        if (!project.name.contains("demo")) {
            plugin(libs.plugins.maven.publish.get().pluginId)
        }
        plugin(libs.plugins.kotlin.plugin.spring.get().pluginId)
        plugin(libs.plugins.kotlin.jvm.get().pluginId)
        plugin(libs.plugins.axionRelease.get().pluginId)
        plugin(libs.plugins.kotlin.kapt.get().pluginId)
//        plugin(libs.plugins.gradleMavenPublishPlugin.get().pluginId)
        // 导入仓库配置
        from(file("$configDir/repositories.gradle.kts"))
    }
//    // 中央仓库
//    mavenPublishing {
//        publishToMavenCentral("https://s01.oss.sonatype.org/", true)
//        signAllPublications()
//    }
    // 发布配置
    if (!project.name.contains("demo")) {
        publishing {
            repositories {
                // 本地仓库
                maven {
                    name = "localRepo"
                    url = uri("file://D:/Config/Jrebel/.jrebel/.m2/repository")
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
                maven {
                    name = "GitLab"
                    url = uri("https://gitlab.snow-lang.com/api/v4/projects/18/packages/maven")
                    credentials(HttpHeaderCredentials::class) {
                        name = "Private-Token"
                        value = System.getenv("PIPELINE_BOT_TOKEN")  // 存储为 GitLab CI/CD Secret
                    }
                    authentication {
                        create("header", HttpHeaderAuthentication::class)
                    }
                }
                // Gitea 仓库
                val host = System.getenv("GITEA_HOST")
                host?.let {
                    maven {
                        name = "Gitea"
                        url = uri("${it}/api/packages/gewuyou/maven")
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
    tasks.named<Jar>("jar") {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
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
/**
 * 注册一个 Gradle 任务用于清理项目中的无用文件。
 *
 * 该任务会清理以下内容：
 * - gradlew 和 gradlew.bat（Gradle 包装器脚本）
 * - gradle-wrapper.jar 和 gradle-wrapper.properties（Gradle 包装器库和配置）
 * - .gradle 和 gradle 目录（Gradle 缓存和配置目录）
 * - src/main/resources/application.properties（开发环境配置文件）
 * - src/test 目录（测试资源）
 */
tasks.register<CleanUselessFilesTask>("cleanUselessFiles") {
    group = "project"
    description = "删除根目录及子模块的无用文件"

    includedProjectPaths.set(rootProject.subprojects.map { it.projectDir.absolutePath })
}

/**
 * 获取项目的布尔属性值。
 *
 * 用于从项目属性中获取指定键对应的布尔值，如果未设置或无法解析为布尔值，则返回默认值 false。
 *
 * @param key 属性键名
 * @return 属性对应的布尔值，若不存在或无效则返回 false
 */
fun Project.getPropertyByBoolean(key: String): Boolean {
    return properties[key]?.toString()?.toBoolean() ?: false
}

/**
 * CleanUselessFilesTask 是一个 Gradle 自定义任务类，用于清理项目中的无用文件。
 *
 * 该任务会遍历所有子项目，并删除预定义的无用文件和目录。
 */
abstract class CleanUselessFilesTask : DefaultTask() {

    /**
     * 获取需要清理的项目路径列表。
     *
     * 每个路径代表一个项目的根目录，任务将基于这些目录查找并删除无用文件。
     */
    @get:Input
    abstract val includedProjectPaths: ListProperty<String>

    /**
     * 执行清理操作的主要方法。
     *
     * 此方法会构建待删除文件列表，并逐个检查是否存在并删除，最后输出删除结果。
     */
    @TaskAction
    fun clean() {
        val targets = mutableListOf<File>()

        // 构建需要清理的文件和目录列表
        includedProjectPaths.get().forEach { path ->
            val projectDir = File(path)
            targets.add(projectDir.resolve("gradlew"))
            targets.add(projectDir.resolve("gradlew.bat"))
            targets.add(projectDir.resolve("gradle/wrapper/gradle-wrapper.jar"))
            targets.add(projectDir.resolve("gradle/wrapper/gradle-wrapper.properties"))
            targets.add(projectDir.resolve(".gradle"))
            targets.add(projectDir.resolve("gradle"))
            targets.add(projectDir.resolve("src/main/resources/application.properties"))
            targets.add(projectDir.resolve("src/test"))
            targets.add(projectDir.resolve("HELP.md"))
            targets.add(projectDir.resolve("settings.gradle.kts"))
        }

        // 执行删除操作并统计结果
        var count = 0
        targets.filter { it.exists() }.forEach {
            logger.lifecycle("删除：${it.absolutePath}")
            count++
            it.deleteRecursively()
        }
        if (count > 0) {
            logger.lifecycle("已删除 $count 个无用文件。")
        } else {
            logger.lifecycle("无无用文件。")
        }
    }
}
