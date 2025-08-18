package com.gewuyou.webmvc.spec.jimmer.service

import com.gewuyou.webmvc.spec.core.service.CrudServiceSpec

/**
 *Jimmer Crud服务规范
 *
 * @since 2025-07-30 21:16:56
 * @author gewuyou
 */
interface JimmerCrudServiceSpec<Entity : Any, Id : Any> : CrudServiceSpec<Entity, Id> {

}