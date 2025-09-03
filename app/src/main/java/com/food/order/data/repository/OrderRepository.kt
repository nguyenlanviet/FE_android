package com.food.order.data.repository

import com.food.order.data.RetrofitClient
import com.food.order.data.request.AddOrderItemRequest
import com.food.order.data.request.OrderRequest

class OrderRepository {

    private val api = RetrofitClient.instance

    suspend fun getOrder(token: String, orderId: String) = api.getOrder(token, orderId)

    suspend fun getMostFavoriteFood(server: String, time: String) = api.getMostFavoriteFood(server, time)

    suspend fun getRevenueByWeek(server: String, time: String) = api.getRevenueByWeek(server, time)

    suspend fun getListOrderInTime(server: String, time: String) = api.getListOrderInTime(server, time)

    suspend fun createOrder(token: String, request: OrderRequest) = api.createOrder(token, request)

    suspend fun cancelOrder(token: String, orderId: String) = api.cancelOrder(token, orderId)

    suspend fun complete(token: String, orderId: String) = api.complete(token, orderId)

    suspend fun listOrders(token: String) = api.listOrders(token)

    suspend fun removeItemFromOrder(token: String, orderId: String, foodId: String) =
        api.removeItemFromOrder(token, orderId, foodId)

    suspend fun addOrderItem(token: String, orderId: String, request: AddOrderItemRequest) =
        api.addOrderItem(token, orderId, request)
}