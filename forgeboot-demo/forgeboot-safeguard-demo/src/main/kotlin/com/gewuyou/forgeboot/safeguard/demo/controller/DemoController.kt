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

package com.gewuyou.forgeboot.safeguard.demo.controller


import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.AttemptLimit
import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.Cooldown
import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.Idempotent
import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.RateLimit
import com.gewuyou.forgeboot.safeguard.core.enums.KeyProcessingMode
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/safeguard")
class SafeguardTestController {

    // ==== 1) RateLimit：按 IP 限流（容量=2/分钟，单次消耗=1） ====
    @GetMapping("/login")
    @RateLimit(
        key = "'ip:' + #ip",   // SpEL: 使用请求参数 ip 作为限流 key
        capacity = "2",        // 令牌桶容量
        refillTokens = "2",    // 每周期补充 2 个
        refillPeriodMs = "60000", // 周期 60 秒
        requested = "1"
    )
    fun login(@RequestParam ip: String): String {
        return "login ok from $ip @${Instant.now()}"
    }

    // ==== 2) Cooldown：发送验证码，成功后 60 秒冷却；业务失败则回滚冷却 ====
    @PostMapping("/otp/send")
    @Cooldown(
        key = "'otp:' + #user",     // 针对用户做冷却
        seconds = "60",             // 冷却 60 秒
        rollbackOn = [BusinessException::class] // 这些异常时撤销冷却
    )
    fun sendOtp(
        @RequestParam user: String,
        @RequestParam(defaultValue = "false") fail: Boolean,
    ): String {
        if (fail) {
            // 模拟下游通道异常 -> 触发冷却回滚
            throw BusinessException("短信通道异常，请稍后重试")
        }
        return "OTP sent to $user @${Instant.now()}"
    }

    // ==== 3) Idempotent：从 Header 读幂等键（推荐） ====
    data class CreateOrderReq(
        val requestId: String? = null, // 也可从 body 取（见下一个接口）
        val amount: BigDecimal,
    )

    // ==== 4) Idempotent：从 Body 里的 requestId 取幂等键（也常见） ====
    @PostMapping("/order/body-idem")
    @Idempotent(
        key = "#req.requestId", // SpEL: 使用请求体里的 requestId
        ttlSeconds = "600"
    )
    fun createOrderWithBodyKey(@RequestBody req: CreateOrderReq): Map<String, Any?> {
        require(!req.requestId.isNullOrBlank()) { "requestId 不能为空" }
        val orderId = UUID.randomUUID().toString()
        return mapOf(
            "orderId" to orderId,
            "amount" to req.amount,
            "ts" to Instant.now().toString(),
            "source" to "body-idem"
        )
    }

    /**
     * 模拟“验证码登录”：
     * - code == "000000" 视为成功（未抛异常 → 成功路径，触发 successReset）
     * - 其他 code 抛异常（→ 失败路径，累计一次尝试）
     *
     * Key 规则：
     * - namespace: "safeguard:al:<ip>"  (由切面根据 IP_KEY 帮你拼)
     * - value: "login:user:<username>" (由 SpEL 生成)
     */
    @PostMapping("/attempt/login")
    @AttemptLimit(
        window = "PT1M",                  // 1 分钟滑窗
        max = 3,                          // 窗口内最多 3 次失败
        lock = "PT5M",                    // 触发后锁定 5 分钟
        escalate = "5:PT1H,8:P1D",        // 阶梯：≥5 锁 1h，≥8 锁 1d
        mode = KeyProcessingMode.IP_KEY,  // 默认命名空间带上 IP
        key = "'login:user:' + #req.username", // value = login:user:<username>
        successReset = true,
    )
    fun login(@RequestBody req: LoginReq): LoginResp {
        if (req.code != "000000") {
            // 任何异常都会走失败路径并累计一次
            throw IllegalArgumentException("验证码错误")
        }
        return LoginResp(true, "登录成功")
    }

    data class LoginReq(
        val username: String,
        val code: String,
    )

    data class LoginResp(
        val success: Boolean,
        val message: String,
    )
}

// 业务异常示例：用于 Cooldown 回滚演示
class BusinessException(message: String) : RuntimeException(message)
