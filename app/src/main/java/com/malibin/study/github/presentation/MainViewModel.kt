package com.malibin.study.github.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.study.github.domain.profile.GithubProfile
import com.malibin.study.github.domain.repository.GithubProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val profileRepository: GithubProfileRepository,
) : ViewModel() {

    val githubId = MutableLiveData("")

    private val _githubProfile = MutableStateFlow<GithubProfile?>(null)
    val githubProfile: StateFlow<GithubProfile?> = _githubProfile

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError = MutableStateFlow<Boolean>(false)
    val isError: StateFlow<Boolean> = _isError

    fun loadGithubProfile() {
        viewModelScope.launch {
            _isLoading.value = true

            profileRepository.getGithubProfile(githubId.value.orEmpty())
                .onSuccess { _githubProfile.value = it }
                .onFailure {
                    _githubProfile.value = null
                    _isError.value = true
                }
            _isLoading.value = false
        }
    }
}
