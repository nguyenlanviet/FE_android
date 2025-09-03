package com.food.order.data

import com.food.order.data.request.AddOrderItemRequest
import com.food.order.data.request.CopyItemsRequest
import com.food.order.data.request.FoodRequest
import com.food.order.data.request.LoginRequest
import com.food.order.data.request.OrderRequest
import com.food.order.data.request.RegisterRequest
import com.food.order.data.request.TableRequest
import com.food.order.data.request.UpdateStaffRequest
import com.food.order.data.response.EmployeeOrderResponse
import com.food.order.data.response.FileUploadResponse
import com.food.order.data.response.ListFoodResponse
import com.food.order.data.response.ListOrderResponse
import com.food.order.data.response.ListTableResponse
import com.food.order.data.response.ListUserResponse
import com.food.order.data.response.MostFavoriteFoodResponse
import com.food.order.data.response.OrderResponse
import com.food.order.data.response.RevenueByWeekResponse
import com.food.order.data.response.SimpleLongResponse
import com.food.order.data.response.SimpleResponse
import com.food.order.data.response.TableInfoResponse
import com.food.order.data.response.TokenResponse
import com.food.order.data.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse

    @GET("users/count-by-server")
    suspend fun getCountEmployee(@Query("server") server: String): SimpleLongResponse

    @POST("users/register")
    suspend fun register(@Header("Authorization") token: String, @Body request: RegisterRequest): TokenResponse

    @GET("users/getInfo")
    suspend fun getInfo(@Header("Authorization") token: String): UserResponse

    @GET("users/")
    suspend fun getUsersFromServer(
        @Header("Authorization") token: String,
        @Query("server") server: String
    ): ListUserResponse

    @PUT("users/{server}/{employeeId}")
    suspend fun updateUserFromServer(
        @Header("Authorization") token: String,
        @Path("server") server: String,
        @Path("employeeId") employeeId: String,
        @Body request: UpdateStaffRequest
    ): SimpleResponse

    @DELETE("users/{server}/{employeeId}")
    suspend fun deleteUserFromServer(
        @Header("Authorization") token: String,
        @Path("server") server: String,
        @Path("employeeId") employeeId: String,
    ): SimpleResponse

    @Multipart
    @POST("files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): FileUploadResponse

    // region -> Food

    @POST("foods/create")
    suspend fun createFood(
        @Header("Authorization") token: String,
        @Body request: FoodRequest
    ): SimpleResponse

    @PUT("foods/{id}")
    suspend fun updateFood(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: FoodRequest
    ): SimpleResponse

    @DELETE("foods/{id}")
    suspend fun deleteFood(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): SimpleResponse

    @GET("foods/list")
    suspend fun getFoodsFromServer(
        @Header("Authorization") token: String,
    ): ListFoodResponse

    // endregion

    // region -> Table

    @POST("tables/create")
    suspend fun createTable(
        @Header("Authorization") token: String,
        @Body request: TableRequest
    ): SimpleResponse

    @PUT("tables/{id}")
    suspend fun updateTable(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: TableRequest
    ): SimpleResponse

    @DELETE("tables/{id}")
    suspend fun deleteTable(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): SimpleResponse

    @GET("tables/list")
    suspend fun getTablesFromServer(
        @Header("Authorization") token: String,
    ): ListTableResponse

    @GET("tables/list-free")
    suspend fun getTablesFreeFromServer(
        @Header("Authorization") token: String,
    ): ListTableResponse

    @GET("tables/{tableId}")
    suspend fun getTableByIdAndServer(
        @Header("Authorization") token: String,
        @Path("tableId") id: String,
    ): TableInfoResponse

    @GET("tables/{tableId}/current-order/creator")
    suspend fun getCreateByOrder(
        @Header("Authorization") token: String,
        @Path("tableId") id: String,
    ): EmployeeOrderResponse

    @POST("tables/copy-items")
    suspend fun copyTableOrder(
        @Header("Authorization") token: String,
        @Body request: CopyItemsRequest
    ): SimpleResponse

    // endregion

    // region -> order

    @GET("orders/{id}")
    suspend fun getOrder(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): OrderResponse

    @GET("orders/most-favorite-food")
    suspend fun getMostFavoriteFood(
        @Query("server") server: String,
        @Query("time") time: String,
    ): MostFavoriteFoodResponse

    @GET("orders/revenue-by-week")
    suspend fun getRevenueByWeek(
        @Query("server") server: String,
        @Query("time") time: String,
    ): RevenueByWeekResponse

    @GET("orders/list")
    suspend fun getListOrderInTime(
        @Query("server") server: String,
        @Query("time") time: String,
    ): ListOrderResponse

    @POST("orders/create")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): SimpleResponse

    @DELETE("orders/{id}")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): SimpleResponse

    @DELETE("orders/{id}/remove-item/{foodId}")
    suspend fun removeItemFromOrder(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("foodId") foodId: String,
    ): SimpleResponse

    @PUT("orders/{id}/add-item")
    suspend fun addOrderItem(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: AddOrderItemRequest
    ): SimpleResponse

    @PUT("orders/{id}/complete")
    suspend fun complete(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): SimpleResponse

    @GET("orders")
    suspend fun listOrders(
        @Header("Authorization") token: String
    ): ListOrderResponse

    // endregion
}