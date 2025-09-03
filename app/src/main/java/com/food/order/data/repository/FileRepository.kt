package com.food.order.data.repository

import com.food.order.data.RetrofitClient
import okhttp3.MultipartBody

class FileRepository {

    private val api = RetrofitClient.instance

    suspend fun uploadImage(file: MultipartBody.Part) = api.uploadImage(file)
}