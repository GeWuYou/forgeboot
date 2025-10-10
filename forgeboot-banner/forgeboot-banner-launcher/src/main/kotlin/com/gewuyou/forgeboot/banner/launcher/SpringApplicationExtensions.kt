/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.banner.launcher

import com.gewuyou.forgeboot.banner.api.config.entities.BannerProperties
import com.gewuyou.forgeboot.banner.impl.ConfigurableBannerProvider
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.StandardEnvironment

/**
 * 使用Forge横幅运行Spring应用的函数
 *
 * @param T Spring应用的主类类型，必须是Any的子类
 * @param args 命令行参数，传递给Spring应用
 */
inline fun <reified T : Any> runApplicationWithForgeBanner(vararg args: String) {
    // 创建并配置Spring应用的环境
    val env = StandardEnvironment()
    // 获取配置属性绑定器
    val binder = Binder.get(env)
    // 从配置中绑定BannerProperties属性，如果没有找到，则使用默认值
    val props = binder.bind("forgeboot.banner", BannerProperties::class.java).orElse(BannerProperties())
    // 创建一个自定义的Banner实例
    val banner = Banner { _, _, out -> ConfigurableBannerProvider(props).printBanner(out) }
    // 构建并运行Spring应用
    SpringApplicationBuilder()
        .sources(T::class.java)
        .banner(banner)
        .environment(env)
        .run(*args)
}
