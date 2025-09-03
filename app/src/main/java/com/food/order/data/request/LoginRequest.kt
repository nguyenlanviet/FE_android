package com.food.order.data.request

data class LoginRequest(
    val employeeId: String,
    val password: String,
    val server: String
)