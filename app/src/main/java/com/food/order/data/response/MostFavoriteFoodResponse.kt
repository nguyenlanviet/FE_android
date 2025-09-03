package com.food.order.data.response

import com.food.order.data.model.OrderItem

data class MostFavoriteFoodResponse(
    val code: Int,
    val message: String,
    val data: OrderItem?,
)