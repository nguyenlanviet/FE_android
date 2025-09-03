package com.food.order.data.response

data class RevenueByWeekResponse(
    val code: Int,
    val message: String,
    val data: List<RevenueByWeek>,
)

data class RevenueByWeek(
    val week: String,
    val total: Double,
)