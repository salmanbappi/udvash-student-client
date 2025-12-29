package com.udvash.student.ui.screens.classes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udvash.student.data.model.ClassItem
import com.udvash.student.data.repository.ClassRepository
import kotlinx.coroutines.launch

class ClassListViewModel : ViewModel() {
    private val _classes = mutableStateOf<List<ClassItem>>(emptyList())
    val classes: State<List<ClassItem>> = _classes

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        fetchClasses()
    }

    private fun fetchClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = ClassRepository.getPastClasses()
            _isLoading.value = false
            
            if (result.isSuccess) {
                _classes.value = result.getOrDefault(emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load classes"
            }
        }
    }
}
