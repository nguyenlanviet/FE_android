package com.food.order.data.repository

import com.food.order.data.RetrofitClient
import com.food.order.data.request.CopyItemsRequest
import com.food.order.data.request.TableRequest

class TableRepository {

    private val api = RetrofitClient.instance

    suspend fun createTable(token: String, request: TableRequest) = api.createTable(token, request)

    suspend fun updateTable(token: String, id: String, request: TableRequest) = api.updateTable(token, id, request)

    suspend fun deleteTable(token: String, id: String) = api.deleteTable(token, id)

    suspend fun getTablesFromServer(token: String) = api.getTablesFromServer(token)

    suspend fun getTablesFreeFromServer(token: String) = api.getTablesFreeFromServer(token)

    suspend fun getTableByIdAndServer(token: String, tableId: String) = api.getTableByIdAndServer(token, tableId)

    suspend fun getCreateByOrder(token: String, tableId: String) = api.getCreateByOrder(token, tableId)

    suspend fun copyTableOrder(token: String, request: CopyItemsRequest) = api.copyTableOrder(token, request)

}