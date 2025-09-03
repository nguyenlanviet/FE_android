package com.food.order.data.model

import java.io.Serializable

data class UserModel(
    val createdAt: String,
    val role: String,
    val displayName: String,
    val employeeId: String,
    val createdBy: String?
) : Serializable