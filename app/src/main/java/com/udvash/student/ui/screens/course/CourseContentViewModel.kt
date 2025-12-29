package com.udvash.student.ui.screens.course

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udvash.student.data.model.CourseContentItem
import com.udvash.student.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseContentViewModel : ViewModel() {
    private val _items = mutableStateOf<List<CourseContentItem>>(emptyList())
    val items: State<List<CourseContentItem>> = _items

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        fetchContent()
    }

    private fun fetchContent() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = CourseRepository.getCourseContent()
            _isLoading.value = false
            
            if (result.isSuccess) {
                _items.value = result.getOrDefault(emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load content"
            }
        }
    }
}
