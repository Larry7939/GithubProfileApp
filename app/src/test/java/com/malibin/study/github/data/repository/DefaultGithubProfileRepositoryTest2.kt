package com.malibin.study.github.data.repository

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.data.source.GithubProfileSource
import com.malibin.study.github.domain.profile.GithubProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class DefaultGithubProfileRepositoryTest2 {
    lateinit var fakeLocalGithubProfileSource: GithubProfileSource
    lateinit var fakeRemoteGithubProfileSource: GithubProfileSource
    lateinit var fakeDefaultGithubProfileRepository: DefaultGithubProfileRepository
    lateinit var gitHubProfile: GithubProfile

    @BeforeEach
    fun setUp() {
        fakeLocalGithubProfileSource = mockk(relaxed = true)
        fakeRemoteGithubProfileSource = mockk(relaxed = true)
        fakeDefaultGithubProfileRepository = DefaultGithubProfileRepository(
            fakeLocalGithubProfileSource,
            fakeRemoteGithubProfileSource
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `로컬 상에 Github Profile이 저장되어있는 경우, Remote 요청을 하지 않는다`() = runTest {
        // given
        gitHubProfile = GithubProfile(
            id = 1L,
            userName = "name1",
            avatarUrl = "https://www.linkpicture.com/view.php?img=LPic63df5f35265bb1785508137",
            name = "name1",
            bio = "Hello",
            followersCount = 1,
            followingCount = 1
        )
        coEvery { fakeLocalGithubProfileSource.getGithubProfile("name1") } returns Result.success(
            gitHubProfile
        )
        // when
        val actualGithubProfile = fakeDefaultGithubProfileRepository.getGithubProfile("name1")

        // then
        assertAll(
            { coVerify(exactly = 0) { fakeRemoteGithubProfileSource.getGithubProfile("name1") } },
            { coVerify(exactly = 0) { fakeLocalGithubProfileSource.saveGithubProfile(gitHubProfile) } },
            { assertThat(actualGithubProfile.getOrNull()).isEqualTo(gitHubProfile) },
            { coVerify(exactly = 1) { fakeLocalGithubProfileSource.getGithubProfile("name1") } }
        )
    }
}
