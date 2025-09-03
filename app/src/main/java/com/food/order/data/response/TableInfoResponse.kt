package com.food.order.data.response

data class TableInfoResponse(
    val code: Int,
    val message: String,
    val data: TableResponse,
)