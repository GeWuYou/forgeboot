package com.gewuyou.webmvc.spec.jimmer.page

import com.gewuyou.webmvc.spec.core.page.Filterable
import org.babyfish.jimmer.sql.ast.query.specification.JSpecification
import org.babyfish.jimmer.sql.ast.table.Table

/**
 *Jimmer J Filenable
 *
 * @since 2025-08-18 07:41:17
 * @author gewuyou
 */
interface JimmerJFilterable<EntityType : Any> : Filterable<JSpecification<EntityType, Table<EntityType>>> {
}