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
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.core.env.StandardEnvironment

/**
 * 使用Forge横幅运行Spring应用的函数
 *
 * @param T Spring应用的主类类型，必须是Any的子类
 * @param args 命令行参数，传递给Spring应用
 */
inline fun <reified T : Any> runApplicationWithForgeBanner(vararg args: String) {
    val env = StandardEnvironment()
    val binder = Binder.get(env)

    // 尝试绑定 forgeboot.banner
    val binding = binder.bind("forgeboot.banner", Bindable.of(BannerProperties::class.java))

    val banner: Banner = if (binding.isBound) {
        // 有配置 → 使用自定义 Banner
        val props = binding.get()
        Banner { _, _, out -> ConfigurableBannerProvider(props).printBanner(out) }
    } else {
        // 没配置 → 回退到默认不显示 Banner 或自定义空 Banner
        Banner { _, _, _ -> } // 空 Banner 实现，等价于禁用 Banner
    }

    SpringApplicationBuilder()
        .sources(T::class.java)
        .banner(banner)
        .environment(env)
        .run(*args)
}
