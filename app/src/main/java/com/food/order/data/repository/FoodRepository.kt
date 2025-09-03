package com.food.order.data.repository

import com.food.order.data.RetrofitClient
import com.food.order.data.request.FoodRequest

class FoodRepository {

    private val api = RetrofitClient.instance

    suspend fun createFood(token: String, request: FoodRequest) = api.createFood(token, request)

    suspend fun updateFood(token: String, id: String, request: FoodRequest) = api.updateFood(token, id, request)

    suspend fun deleteFood(token: String, id: String) = api.deleteFood(token, id)

    suspend fun getFoodsFromServer(token: String) = api.getFoodsFromServer(token)

}