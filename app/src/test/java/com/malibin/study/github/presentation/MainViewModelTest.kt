package com.malibin.study.github.presentation

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.domain.profile.GithubProfile
import com.malibin.study.github.domain.repository.GithubProfileRepository
import com.malibin.study.github.utils.InstantTaskExecutorExtension
import com.malibin.study.github.utils.MainCoroutineExtension
import com.malibin.study.github.utils.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val instantTaskExecutorExtension = InstantTaskExecutorExtension()

        @JvmField
        @RegisterExtension
        val coroutineExtension = MainCoroutineExtension()
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var githubProfileRepository: GithubProfileRepository

    @BeforeEach
    fun setUp() {
        githubProfileRepository = mockk(relaxed = true)
        mainViewModel = MainViewModel(githubProfileRepository)
    }

    @Test
    fun `깃헙 프로필을 가져올 수 있다`() = runBlocking {
        // given
        val expectedGithubProfile = GithubProfile(
            id = 0L,
            userName = "malibinYun",
            avatarUrl = "url",
            name = "Yun Hyeok",
            bio = "bio",
            followersCount = 999,
            followingCount = 111,
        )

        coEvery {
            githubProfileRepository.getGithubProfile(any())
        } returns Result.success(expectedGithubProfile)

        // when
        mainViewModel.loadGithubProfile()

        // then
        assertThat(mainViewModel.githubProfile.getOrAwaitValue()).isEqualTo(expectedGithubProfile)
    }

    @Test
    fun `깃헙 프로필을 가져오지 못하면 에러를 나타낸다`() = runBlocking {
        // given
        coEvery {
            githubProfileRepository.getGithubProfile(any())
        } returns Result.failure(IllegalArgumentException())

        // when
        mainViewModel.loadGithubProfile()

        // then
        assertAll(
            { assertThat(mainViewModel.githubProfile.getOrAwaitValue()).isEqualTo(null) },
            { assertThat(mainViewModel.isError.getOrAwaitValue()).isTrue() }
        )
    }
}
