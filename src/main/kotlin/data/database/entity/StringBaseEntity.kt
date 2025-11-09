package com.sukakotlin.data.database.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID

open class StringBaseEntity(id: EntityID<String>): BaseEntity<String>(id)
