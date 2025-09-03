package com.food.order.data.mapper

import com.food.order.data.model.FoodModel
import com.food.order.data.response.FoodResponse

fun FoodResponse.toFoodModel(): FoodModel {
    return FoodModel(
        id = id,
        foodName = foodName,
        image = image,
        price = price,
        unit = unit,
        category = category,
        createdBy = createdBy,
        createdAt = createdAt,
        server = server,
        description = description
    )
}