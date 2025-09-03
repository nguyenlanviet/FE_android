package com.food.order.data.repository

import com.food.order.data.RetrofitClient
import com.food.order.data.request.LoginRequest
import com.food.order.data.request.RegisterRequest
import com.food.order.data.request.UpdateStaffRequest

class UserRepository {
    private val api = RetrofitClient.instance

    suspend fun login(request: LoginRequest) = api.login(request)

    suspend fun register(token: String, request: RegisterRequest) = api.register(token, request)

    suspend fun getInfo(token: String) = api.getInfo(token)

    suspend fun getCountEmployee(server: String) = api.getCountEmployee(server)

    suspend fun getUsersFromServer(token: String, server: String) = api.getUsersFromServer(token, server)

    suspend fun updateUserFromServer(
        token: String,
        server: String,
        employeeId: String,
        request: UpdateStaffRequest
    ) = api.updateUserFromServer(token, server, employeeId, request)

    suspend fun deleteUserFromServer(
        token: String,
        server: String,
        employeeId: String,
    ) = api.deleteUserFromServer(token, server, employeeId)
}