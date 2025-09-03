package com.food.order.data.mapper

import com.food.order.data.model.TableModel
import com.food.order.data.response.TableResponse

fun TableResponse.toTableModel(): TableModel {
    return TableModel(
        id = id,
        name = tableName,
        currentOrderId = currentOrderId,
    )
}