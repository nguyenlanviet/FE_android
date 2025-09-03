package com.food.order.data.request

data class RegisterRequest(
    val employeeId: String,
    val displayName: String,
    val role: String,
    val createdBy: String?,
    val password: String,
    val server: String
)