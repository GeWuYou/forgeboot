extra {
    // 标记为根项目
    setProperty(ProjectFlags.IS_ROOT_MODULE, true)
}

dependencies {
    api(project(Modules.Webmvc.VERSION_STARTER))
    api(project(Modules.Webmvc.LOGGER_STARTER))
}
