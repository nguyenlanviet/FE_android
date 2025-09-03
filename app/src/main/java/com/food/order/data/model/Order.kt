package com.food.order.data.model

data class Order(
    val id: String = "",
    val tableId: String = "",
    val tableName: String = "",
    val server: String = "",
    val createdAt: String = "",
    val createdBy: String = "",
    val employeeName: String = "",
    val status: String = "",
    val items: List<OrderItem> = ArrayList(),
    val totalAmount: Double = 0.0,
)