package com.food.order.data.response

data class TableResponse(
    val id: String,
    val tableName: String,
    val currentOrderId: String?,
)