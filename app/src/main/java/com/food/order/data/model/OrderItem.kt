package com.food.order.data.model

data class OrderItem(
    val foodId: String = "",
    val foodImage: String = "",
    val foodName: String = "",
    val unit: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val note: String = "",
)
