package com.malibin.study.github.presentation

import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.domain.profile.GithubProfile
import com.malibin.study.github.domain.repository.GithubProfileRepository
import com.malibin.study.github.utils.CoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelTest2 : ViewModel() {
    lateinit var fakeProfileRepository: GithubProfileRepository
    lateinit var mainViewModel: MainViewModel
    lateinit var githubProfile: GithubProfile

    @BeforeEach
    fun `setUp`() {
        fakeProfileRepository = mockk<GithubProfileRepository>(relaxed = true)
        mainViewModel = MainViewModel(fakeProfileRepository)
        githubProfile = GithubProfile(
            id = 1L,
            userName = "username",
            avatarUrl = "avartarUrl",
            name = "name",
            bio = "bio",
            followingCount = 1,
            followersCount = 1
        )
    }

    @Test
    fun `loadGithubProfile 호출 성공 시, GithubProfile을 가져온다`() = runTest {
        //given
        coEvery { fakeProfileRepository.getGithubProfile(any()) }.returns(
            Result.success(
                githubProfile
            )
        )
        //when
        mainViewModel.loadGithubProfile()
        advanceUntilIdle()

        //then
        assertAll(
            { assertThat(mainViewModel.githubProfile.value).isEqualTo(githubProfile) },
            { coVerify(exactly = 1) { fakeProfileRepository.getGithubProfile(any()) } }
        )
    }

    @Test
    fun `loadGithubProfile 메소드 호출 실패 시 isError의 value 가 True가 된다`() = runTest {
        //given
        coEvery { fakeProfileRepository.getGithubProfile(any()) }.returns(
            Result.failure(
                IllegalArgumentException()
            )
        )
        //when
        mainViewModel.loadGithubProfile()
        advanceUntilIdle()
        //then
        assertThat(mainViewModel.isError.value).isEqualTo(true)
    }


    companion object {
        @JvmStatic
        @RegisterExtension
        val rule = CoroutinesTestRule()
    }
}