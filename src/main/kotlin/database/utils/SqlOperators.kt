package com.sukakotlin.database.utils

import org.jetbrains.exposed.v1.core.ComparisonOp
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.ExpressionWithColumnType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.QueryParameter

class ILikeOp(expr1: Expression<*>, expr2: Expression<*>): ComparisonOp(expr1, expr2, "ILIKE")

infix fun ExpressionWithColumnType<String>.ilike(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter(pattern, this.columnType))

infix fun ExpressionWithColumnType<String>.ilikeFull(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter(pattern, this.columnType))

infix fun ExpressionWithColumnType<String>.ilikeContains(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter("%$pattern%", this.columnType))

infix fun ExpressionWithColumnType<String>.ilikeStartsWith(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter("$pattern%", this.columnType))

infix fun ExpressionWithColumnType<String>.ilikeEndsWith(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter("%$pattern", this.columnType))
