package com.food.order.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.food.order.data.model.UserModel
import com.food.order.data.repository.UserRepository
import com.food.order.data.request.LoginRequest
import com.food.order.data.response.TokenResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userFlow = MutableSharedFlow<UserModel>(replay = 0)
    val userFlow = _userFlow.asSharedFlow()

    private val _tokenFlow = MutableSharedFlow<TokenResponse>(replay = 0)
    val tokenFlow = _tokenFlow.asSharedFlow()

    private val _loadingFlow = MutableSharedFlow<Boolean>(replay = 0)
    val loadingFlow = _loadingFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loadingFlow.emit(true)
            try {
                val token = repository.login(request)
                _tokenFlow.emit(token)
                getInfo(token.token)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }

    private fun getInfo(token: String) {
        viewModelScope.launch {
            _loadingFlow.emit(true)

            try {
                val user = repository.getInfo("Bearer $token")
                _userFlow.emit(user.toUserModel())
                _loadingFlow.emit(false)
            } catch (e: Exception) {
                _errorFlow.emit("Failed: ${e.message}")
                _loadingFlow.emit(false)
            }
        }
    }
}