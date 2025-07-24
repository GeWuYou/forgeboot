package com.gewuyou.webmvc.spec.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * Jpa CRUD 存储库规范
 *
 * @since 2025-05-29 20:39:11
 * @author gewuyou
 */
interface JpaCrudRepositorySpec<Entity, Id>:  JpaRepository<Entity, Id>, JpaSpecificationExecutor<Entity>