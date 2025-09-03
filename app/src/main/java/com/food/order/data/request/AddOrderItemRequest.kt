package com.food.order.data.request

data class AddOrderItemRequest(
    val foodId: String,
    val foodImage: String,
    val foodName: String,
    val unit: String,
    val price: Double,
    val quantity: Int,
    val note: String,
)