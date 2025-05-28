/**
 * 本地化键生成插件
 *
 * 该插件用于自动生成本地化键的代码，以简化国际化(i18n)过程
 * 它通过读取属性文件来生成一个包含所有本地化键的Kotlin文件
 * 此方法便于集中管理和自动生成代码，避免手动编写易出错的字符串字面量
 *
 * @since 2025-05-28 17:11:40
 * @author gewuyou
 */
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.util.*

/**
 * 可缓存的任务类，用于生成本地化键
 *
 * 该任务读取输入文件中的属性，并根据这些属性生成一个Kotlin文件
 * 文件中包含根据属性键生成的常量，这些键按指定的分组深度和级别进行组织
 */
@CacheableTask
abstract class GenerateI18nKeysTask : DefaultTask() {

    /**
     * 输入文件属性，包含本地化属性
     * 被注解为@InputFile和@PathSensitive，以确保Gradle可以正确地检测文件路径的变化
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    /**
     * 输出文件属性，将生成的代码写入此文件
     * 被注解为@OutputFile，以告知Gradle该文件是任务的输出
     */
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    /**
     * 根包名属性，用于指定生成代码的包路径
     * 被注解为@Input，因为包路径的变化应导致任务重新执行
     */
    @get:Input
    abstract val rootPackage: Property<String>

    /**
     * 层级属性，决定键的分组深度
     * 注解为@Input，因为层级变化会影响生成的代码结构
     */
    @get:Input
    abstract val level: Property<Int>

    /**
     * 任务的主要执行方法
     *
     * 该方法读取输入文件中的属性，然后根据这些属性生成一个Kotlin文件
     * 它首先加载属性，然后根据指定的包路径、层级和分组深度生成代码
     */
    @TaskAction
    fun generate() {
        // 加载属性文件
        val props = Properties().apply {
            inputFile.get().asFile.inputStream().use { load(it) }
        }

        // 准备输出文件
        val output = outputFile.get().asFile
        output.parentFile.mkdirs()

        // 处理属性键，按分组深度分组
        val keys = props.stringPropertyNames().sorted()
        val levelDepth = level.get()
        val indentUnit = "    "

        fun Appendable.generateObjects(keys: List<String>, depth: Int) {
            val groups = keys.groupBy { it.split(".").take(depth).joinToString(".") }

            groups.forEach { (groupKey, groupKeys) ->
                val segments = groupKey.split(".")
                val indent = indentUnit.repeat(segments.size)
                val objectName = segments.last().replaceFirstChar(Char::uppercase)

                appendLine("$indent object $objectName {")

                val deeperKeys = groupKeys.filter { it.split(".").size > depth }

                if (depth < levelDepth && deeperKeys.isNotEmpty()) {
                    generateObjects(deeperKeys, depth + 1)
                } else {
                    groupKeys.forEach { fullKey ->
                        val constName = fullKey
                            .split(".")
                            .drop(levelDepth)
                            .joinToString("_") { it.uppercase() }
                            .ifBlank { fullKey.replace(".", "_").uppercase() }

                        appendLine("${indent}$indentUnit const val $constName = \"$fullKey\"")
                    }
                }

                appendLine("$indent }")
            }
        }
        // 生成代码并写入输出文件
        output.writeText(buildString {
            appendLine("package ${rootPackage.get()}")
            appendLine("// ⚠️ Auto-generated. Do not edit manually.")
            appendLine("object I18nKeys {")
            generateObjects(keys, 1)
            appendLine("}")
        })

        println("✅ Generated I18nKeys.kt at ${output.absolutePath}")
    }
}

/**
 * 扩展类，用于配置插件
 *
 * 该扩展定义了插件的可配置属性，包括层级、分组深度和根包名
 */
interface I18nKeyGenExtension {
    val level: Property<Int>
    val rootPackage: Property<String>
    val inputPath: Property<String>
    val fileName: Property<String>
    val readPath: Property<String>
    val readFileName: Property<String>
}

/**
 * 本地化键生成插件的实现
 *
 * 该插件在项目中添加扩展和任务，以生成本地化键的代码
 */
// ---- 插件主体 ----
class I18nKeyGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("i18nKeyGen", I18nKeyGenExtension::class.java).apply {
            level.convention(2)
            rootPackage.convention("com.gewuyou.i18n")
            inputPath.convention("generated/i18n") // 子路径
            fileName.convention("I18nKeys.kt")
            readPath.convention("src/main/resources/i18n")
            readFileName.convention("messages.properties")
        }

        project.afterEvaluate {
            val propsFile = project.file("${ext.readPath.get()}/${ext.readFileName.get()}")
            if (!propsFile.exists()) return@afterEvaluate

            // 输出文件路径
            val outputFile = project.layout.buildDirectory.file("${ext.inputPath.get()}/${ext.fileName.get()}")
            val outputDir = project.layout.buildDirectory.dir(ext.inputPath)

            // 注册生成任务
            val generateTask = project.tasks.register("generateI18nKeys", GenerateI18nKeysTask::class.java) {
                setProperty("inputFile", propsFile)
                setProperty("outputFile", outputFile)
                setProperty("level", ext.level)
                setProperty("rootPackage", ext.rootPackage)
            }

            // 👇 确保编译前依赖该任务
            listOf("compileKotlin", "kaptGenerateStubsKotlin").forEach { name ->
                project.tasks.matching { it.name == name }.configureEach {
                    dependsOn(generateTask)
                }
            }

            // 👇 延迟注册源码目录：使用 map 避免提前访问目录
            project.plugins.withId("org.jetbrains.kotlin.jvm") {
                val kotlinExt = project.extensions.getByType(KotlinProjectExtension::class.java)
                kotlinExt.sourceSets.getByName("main").kotlin.srcDir(outputDir.map { it.asFile })
            }
        }
    }
}

