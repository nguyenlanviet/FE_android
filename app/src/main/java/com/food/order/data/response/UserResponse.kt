package com.food.order.data.response

import com.food.order.data.model.UserModel
import java.io.Serializable

data class UserResponse(
    val employeeId: String,
    val displayName: String,
    val role: String,
    val createdAt: String,
    val createdBy: String?
) : Serializable {
    fun toUserModel(): UserModel {
        return UserModel(
            createdAt = createdAt,
            role = role,
            displayName = displayName,
            employeeId = employeeId,
            createdBy = createdBy
        )
    }

}