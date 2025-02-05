package com.malibin.study.github.presentation.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface CountMemory {
    fun save(history: String)
    fun getHistories(): List<String>
}

class TestViewModel(private val countMemory: CountMemory) : ViewModel() {
    private val _count = MutableStateFlow<Int>(0)
    val count: StateFlow<Int> get() = _count

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> get() = _history
    fun increase() {
        viewModelScope.launch {
                _count.value += 1
                countMemory.save("increase")

        }
    }

    fun decrease() {
        viewModelScope.launch {
            _count.value -= 1
            countMemory.save("decrease")
        }
    }

    fun loadHistories() {
        viewModelScope.launch {
            _history.value = countMemory.getHistories()
        }
    }

}