package com.food.order.data.response

data class ListFoodResponse(
    val code: Int,
    val message: String,
    val data: List<FoodResponse>,
)