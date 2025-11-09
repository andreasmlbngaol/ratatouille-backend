package com.sukakotlin.data.database.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID

open class IntBaseEntity(id: EntityID<Int>): BaseEntity<Int>(id)
