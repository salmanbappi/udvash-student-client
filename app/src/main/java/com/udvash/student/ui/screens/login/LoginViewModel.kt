package com.udvash.student.ui.screens.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udvash.student.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess: State<Boolean> = _loginSuccess

    fun login(regNo: String, password: String) {
        if (regNo.isBlank() || password.isBlank()) {
            _error.value = "Please enter both Registration Number and Password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = AuthRepository.performLogin(regNo, password)
            
            _isLoading.value = false
            if (result.isSuccess) {
                _loginSuccess.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error"
            }
        }
    }
}
