// This task creates a jar file with the source code of the project

tasks.register<Jar>("sourceTask") {
    logger.info("正在配置${project.name}源代码 jar 文件...")

    // 收集所有源代码（包括 Kotlin 和 Java）
    val sourceSet = project.extensions.getByType<SourceSetContainer>()["main"]
    from(sourceSet.allSource) {
        into("sources") // 将所有源代码放入子目录 sources
    }

    archiveClassifier.set("sources") // 设置生成文件的分类标识

    logger.info("正在创建${project.name}源代码 jar 文件...")
    logger.info("创建${project.name}源代码 jar 文件完成！")
}.configure {
    group = "source"
    description = "使用项目的源代码创建源代码 jar 文件"
}