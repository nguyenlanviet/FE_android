package com.food.order.data.response

data class TokenResponse(
    val code: Int,
    val message: String,
    val token: String
)