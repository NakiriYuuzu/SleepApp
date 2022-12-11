package com.example.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private var _state = MutableStateFlow<LoginState>(LoginState.Empty)
    val state = _state.asStateFlow()

    fun login(acc: String, pwd: String) = viewModelScope.launch {
        _state.value = LoginState.Loading

        delay(1000)

        if (acc.isBlank() || pwd.isBlank()) {
            _state.value = LoginState.Error("帳號或密碼不得為空")
            return@launch
        }

        if (acc == "admin" && pwd == "123456") {
            _state.value = LoginState.Success
        } else if (acc == "flower" && pwd == "flower") {
            _state.value = LoginState.Success
        } else {
            _state.value = LoginState.Error("帳號或密碼錯誤")
        }
    }


    sealed class LoginState {
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
        object Loading : LoginState()
        object Empty : LoginState()
    }
}
