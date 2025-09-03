package com.food.order.data.response

import com.food.order.data.model.Order

data class ListOrderResponse(
    val code: Int,
    val message: String,
    val data: List<Order>
)