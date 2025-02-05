package com.malibin.study.github.presentation

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.domain.profile.GithubProfile
import com.malibin.study.github.domain.repository.GithubProfileRepository
import com.malibin.study.github.utils.InstantTaskExecutorExtension
import com.malibin.study.github.utils.MainCoroutineExtension
import com.malibin.study.github.utils.getOrAwaitValue
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainViewModelTest {

    companion object {
        @JvmStatic
        @RegisterExtension
        val instantTaskExecutorExtension = InstantTaskExecutorExtension()

        @JvmStatic
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    private lateinit var fakeMainViewModel: MainViewModel
    private lateinit var fakeProfileRepository: GithubProfileRepository

    @BeforeEach
    fun setUp() {
        fakeProfileRepository = mockk(relaxed = true)
        fakeMainViewModel = MainViewModel(fakeProfileRepository)
    }

    @Test
    fun `loadGithubProfile 메소드 호출 성공 시 GithubProfile을 가져온다`() = runBlocking {
        //given
        val expectedGithubProfile = GithubProfile(
            id = 1L,
            userName = "name1",
            avatarUrl = "https://www.linkpicture.com/view.php?img=LPic63df5f35265bb1785508137",
            name = "name1",
            bio = "Hello",
            followersCount = 1,
            followingCount = 1
        )
        coEvery {
            fakeProfileRepository.getGithubProfile(any())
        } returns Result.success(expectedGithubProfile)

        //when
        fakeMainViewModel.loadGithubProfile()

        //then
        assertAll(
            { coVerify(exactly = 1) { fakeProfileRepository.getGithubProfile(any()) } },
            {
                assertThat(fakeMainViewModel.githubProfile.value).isEqualTo(
                    expectedGithubProfile
                )
            }
        )
    }

    @Test
    fun `loadGithubProfile 메소드 호출 실패 시 isError의 value 가 True가 된다`() {
        //given
        coEvery {
            fakeProfileRepository.getGithubProfile(any())
        } returns Result.failure(IllegalArgumentException())
        //when
        fakeMainViewModel.loadGithubProfile()
        //then
        assertAll(
            { coVerify(exactly = 1) { fakeProfileRepository.getGithubProfile(any()) } },
            { assertThat(fakeMainViewModel.isError.value).isTrue() }
        )
    }
}