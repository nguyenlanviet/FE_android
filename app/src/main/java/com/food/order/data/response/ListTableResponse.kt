package com.food.order.data.response

data class ListTableResponse(
    val code: Int,
    val message: String,
    val data: List<TableResponse>,
)