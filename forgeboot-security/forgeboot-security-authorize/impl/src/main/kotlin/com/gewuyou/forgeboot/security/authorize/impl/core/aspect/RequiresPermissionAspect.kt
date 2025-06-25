package com.gewuyou.forgeboot.security.authorize.impl.core.aspect

import com.gewuyou.forgeboot.security.authorize.api.core.annotations.RequiresPermission
import com.gewuyou.forgeboot.security.authorize.api.core.manager.AccessManager
import com.gewuyou.forgeboot.security.authorize.api.core.strategy.AuthorizationStrategy
import com.gewuyou.forgeboot.security.authorize.impl.core.resolver.SpELResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder

/**
 * 授权切面
 *
 * 该切面用于处理带有 [RequiresPermission] 注解的方法，执行权限校验逻辑。
 * 通过查找适用的 [AuthorizationStrategy] 来决定是否允许当前用户执行目标方法。
 *
 * @property spELResolver SpEL 表达式解析器，用于动态解析权限字符串。
 * @property accessManager 访问控制管理器，用于最终的权限判断。
 *
 * @since 2025-06-15 13:17:18
 * @author gewuyou
 */
@Aspect
class RequiresPermissionAspect(
    private val spELResolver: SpELResolver,
    private val accessManager: AccessManager
) {

    /**
     * 环绕通知方法，处理带有 [RequiresPermission] 注解的方法调用。
     *
     * 执行流程：
     * 1. 获取当前用户的认证信息；
     * 2. 获取目标方法及其参数；
     * 3. 根据注解配置决定是否解析 SpEL 表达式获取实际权限标识；
     * 4. 查找适用的授权策略并执行授权逻辑；
     * 5. 如果授权成功，则继续执行目标方法。
     *
     * @param joinPoint ProceedingJoinPoint，代表被拦截的方法执行连接点。
     * @param permission 方法上标注的 [RequiresPermission] 注解实例。
     * @return 目标方法的返回值。
     */
    @Around("@annotation(permission)")
    fun around(joinPoint: ProceedingJoinPoint, permission: RequiresPermission): Any {
        // 获取当前用户的安全上下文认证信息
        val auth = SecurityContextHolder.getContext().authentication

        // 根据 dynamic 配置决定是否解析 SpEL 表达式，得到最终权限标识
        val perm = if (permission.dynamic) {
            resolveSpEl(permission.value, joinPoint)
        } else permission.value

        // 使用 accessManager 进行权限校验，失败时抛出访问拒绝异常
        if (!accessManager.hasPermission(auth, perm))
            throw AccessDeniedException("权限不足，需要 [$perm]")

        // 权限校验通过，继续执行目标方法
        return joinPoint.proceed()
    }

    /**
     * 解析 SpEL 表达式，形参名可直接用 #变量 方式引用
     *
     * 例：@RequiresPermission(dynamic = true, value = "'article:read:' + #articleId")
     *
     * @param expression 要解析的 SpEL 表达式。
     * @param joinPoint ProceedingJoinPoint，从中提取方法参数。
     * @return 解析后的实际权限字符串。
     */
    private fun resolveSpEl(expression: String, joinPoint: ProceedingJoinPoint): String {
        val methodSignature = joinPoint.signature as MethodSignature
        val paramNames      = methodSignature.parameterNames          // ["articleId", "commentId", …]
        val args            = joinPoint.args                          // [123L, 456L, …]

        // 组装成 Map<String, Any?>
        val argsMap = paramNames
            .mapIndexed { index, name -> name to args[index] }
            .toMap()

        return spELResolver.parse(expression, argsMap)
    }
}