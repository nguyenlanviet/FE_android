package com.food.order.data.request

data class UpdateStaffRequest(
    val displayName: String?,
    val password: String?,
    val role: String?
)