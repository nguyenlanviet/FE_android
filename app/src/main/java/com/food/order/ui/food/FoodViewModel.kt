package com.food.order.ui.food

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.model.FoodModel
import com.food.order.data.repository.FileRepository
import com.food.order.data.repository.FoodRepository
import com.food.order.data.repository.OrderRepository
import com.food.order.data.request.AddOrderItemRequest
import com.food.order.data.request.FoodRequest
import com.food.order.data.response.FoodResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class FoodViewModel : ViewModel() {

    private val fileRepository = FileRepository()
    private val repository = FoodRepository()
    private val orderRepository = OrderRepository()

    private val _imageUriFlow = MutableSharedFlow<Uri?>(replay = 0)
    val imageUriFlow = _imageUriFlow.asSharedFlow()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(replay = 0)
    val errorFlow = _errorFlow.asSharedFlow()

    private val _foodsFlow = MutableSharedFlow<List<FoodResponse>>(replay = 0)
    val foodsFlow = _foodsFlow.asSharedFlow()

    private val _insertFlow = MutableSharedFlow<Boolean>()
    val insertFlow = _insertFlow.asSharedFlow()

    private val _foodFlow = MutableSharedFlow<FoodModel?>()
    val foodFlow = _foodFlow.asSharedFlow()

    private val _updateFlow = MutableSharedFlow<Boolean>()
    val updateFlow = _updateFlow.asSharedFlow()

    private val _deleteFlow = MutableSharedFlow<Boolean>()
    val deleteFlow = _deleteFlow.asSharedFlow()

    private val _addOrderItemFlow = MutableSharedFlow<Boolean>()
    val addOrderItemFlow = _addOrderItemFlow.asSharedFlow()

    var imageUri: Uri? = null
        private set

    fun setImageUri(uri: Uri) {
        imageUri = uri
        viewModelScope.launch {
            _imageUriFlow.emit(imageUri)
        }
    }

    fun createFood(context: Context, token: String, request: FoodRequest) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val filePart: MultipartBody.Part? = prepareFilePart(context, imageUri!!)
                if (filePart != null) {
                    val fileResponse = fileRepository.uploadImage(filePart)
                    if (fileResponse.success) {
                        val newRequest = request.copy(image = fileResponse.url ?: "")
                        val response = repository.createFood(token, newRequest)
                        if (response.code == 0) {
                            _insertFlow.emit(true)
                        } else {
                            _errorFlow.emit(response.message)
                        }
                    } else {
                        _errorFlow.emit("Failed to upload image")
                    }
                } else {
                    _errorFlow.emit("No image selected")
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    fun updateFood(context: Context, token: String, request: FoodRequest) {
        viewModelScope.launch {
            if (editFood == null) {
                _errorFlow.emit("Food is null")
                return@launch
            }
            _loadingFlow.emit(true)
            try {
                if (imageUri != null) {
                    val filePart: MultipartBody.Part? = prepareFilePart(context, imageUri!!)
                    if (filePart != null) {
                        val fileResponse = fileRepository.uploadImage(filePart)
                        if (fileResponse.success) {
                            val newRequest = request.copy(image = fileResponse.url ?: "")
                            val response = repository.updateFood(token, editFood!!.id, newRequest)
                            if (response.code == 0) {
                                _updateFlow.emit(true)
                            } else {
                                _errorFlow.emit(response.message)
                            }
                        } else {
                            _errorFlow.emit("Failed to upload image")
                        }
                    } else {
                        _errorFlow.emit("No image selected")
                    }
                } else {
                    val response = repository.updateFood(token, editFood!!.id, request)
                    if (response.code == 0) {
                        _updateFlow.emit(true)
                    } else {
                        _errorFlow.emit(response.message)
                    }
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    fun getFoodsFromServer(token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = repository.getFoodsFromServer(token)
                if (response.code == 0) {
                    _foodsFlow.emit(response.data)
                } else {
                    _errorFlow.emit(response.message)
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    private fun prepareFilePart(context: Context, uri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)

        // Đọc nội dung từ URI
        val inputStream: InputStream?
        try {
            inputStream = contentResolver.openInputStream(uri)
            var inputData: ByteArray? = null
            if (inputStream != null) {
                inputData = getBytes(inputStream)
            }

            var requestBody: RequestBody? = null
            if (mimeType != null) {
                if (inputData != null) {
                    requestBody = inputData
                        .toRequestBody(
                            mimeType.toMediaTypeOrNull(),
                            0,
                            inputData.size
                        )
                }
            }

            val fileName = getFileName(context, uri)
            if (requestBody != null) {
                return MultipartBody.Part.createFormData("file", fileName, requestBody)
            }
            return null
        } catch (e: IOException) {
            return null
        }
    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len: Int
        while ((inputStream.read(buffer).also { len = it }) != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if ("content" == uri.scheme) {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }

    var editFood: FoodModel? = null
        private set

    fun setArgument(bundle: Bundle?) {
        viewModelScope.launch {
            bundle?.let {
                if (it.containsKey("edit_food")) {
                    editFood = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getSerializable("edit_food", FoodModel::class.java)
                    } else {
                        it.getSerializable("edit_food") as FoodModel
                    }
                    _foodFlow.emit(editFood!!)
                }
            }
        }
    }

    fun deleteFood(token: String) {
        viewModelScope.launch {
            if (token.isEmpty() || editFood == null) {
                _errorFlow.emit("Token is empty")
            }
            _loadingFlow.emit(true)
            try {
                val response = repository.deleteFood(token, editFood!!.id)
                _deleteFlow.emit(response.code == 0)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }

    fun addOrderItem(token: String, orderId: String, food: FoodModel) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val response = orderRepository.addOrderItem(
                    token, orderId,
                    AddOrderItemRequest(
                        foodId = food.id,
                        foodImage = food.image,
                        foodName = food.foodName,
                        unit = food.unit,
                        price = food.price,
                        quantity = 1,
                        note = ""
                    )
                )
                if (response.code == 0) {
                    _addOrderItemFlow.emit(true)
                } else {
                    _errorFlow.emit(response.message)
                }
            } catch (e: Exception) {
                _errorFlow.emit(e.message ?: "Unknown error")
            } finally {
                _loadingFlow.emit(false)
            }
        }
    }
}