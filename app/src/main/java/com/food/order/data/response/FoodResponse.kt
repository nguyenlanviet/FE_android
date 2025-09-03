package com.food.order.data.response

data class FoodResponse(
    val id: String,
    val foodName: String,
    val image: String,
    val price: Double,
    val unit: String,
    val category: String,
    val createdBy: String,
    val createdAt: String,
    val server: String,
    val description: String?,
)