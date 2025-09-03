package com.food.order.data.response

data class ListUserResponse(
    val code: Int,
    val message: String,
    val data: List<UserResponse>,
)