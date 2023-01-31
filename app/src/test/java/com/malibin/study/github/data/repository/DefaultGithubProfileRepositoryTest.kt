package com.malibin.study.github.data.repository

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.data.source.GithubProfileSource
import com.malibin.study.github.domain.profile.GithubProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class DefaultGithubProfileRepositoryTest {

    private lateinit var remoteGithubProfileSource: GithubProfileSource
    private lateinit var localGithubProfileSource: GithubProfileSource

    private lateinit var defaultGithubProfileRepository: DefaultGithubProfileRepository

    @BeforeEach
    fun setUp() {
        remoteGithubProfileSource = mockk(relaxed = true)
        localGithubProfileSource = mockk(relaxed = true)

        defaultGithubProfileRepository = DefaultGithubProfileRepository(
            localGithubProfileSource = localGithubProfileSource,
            remoteGithubProfileSource = remoteGithubProfileSource,
        )
    }

    @Test
    fun `로컬에서 github Id에 대한 profile을 찾을 수 없으면 remote에서 가져온다`() = runBlocking {
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
        val exception = IllegalArgumentException()

        coEvery {
            localGithubProfileSource.getGithubProfile(any())
        } returns Result.failure(exception)

        coEvery {
            remoteGithubProfileSource.getGithubProfile("malibinYun")
        } returns Result.success(expectedGithubProfile)

        // when
        val actualResult = defaultGithubProfileRepository.getGithubProfile("malibinYun")

        // then
        assertAll(
            { coVerify(exactly = 1) { localGithubProfileSource.getGithubProfile("malibinYun") } },
            { coVerify(exactly = 1) { remoteGithubProfileSource.getGithubProfile("malibinYun") } },
            { assertThat(actualResult.getOrNull()).isEqualTo(expectedGithubProfile) },
        )
    }

    @Test
    fun `로컬에서 github Id에 대한 profile을 찾으면 바로 반환한다`() = runBlocking {
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
        val exception = IllegalArgumentException()

        coEvery {
            localGithubProfileSource.getGithubProfile(any())
        } returns Result.success(expectedGithubProfile)

        coEvery {
            remoteGithubProfileSource.getGithubProfile("malibinYun")
        } returns Result.failure(exception)

        // when
        val actualResult = defaultGithubProfileRepository.getGithubProfile("malibinYun")

        // then
        assertAll(
            { coVerify(exactly = 1) { localGithubProfileSource.getGithubProfile("malibinYun") } },
            { coVerify(exactly = 0) { remoteGithubProfileSource.getGithubProfile("malibinYun") } },
            { assertThat(actualResult.getOrNull()).isEqualTo(expectedGithubProfile) },
        )
    }

    @Test
    fun `githubProfile 저장은 local에만 한다`() = runBlocking {
        // given
        val githubProfile = GithubProfile(
            id = 0L,
            userName = "malibinYun",
            avatarUrl = "url",
            name = "Yun Hyeok",
            bio = "bio",
            followersCount = 999,
            followingCount = 111,
        )

        val slot = slot<GithubProfile>()

        coEvery {
            localGithubProfileSource.saveGithubProfile(capture(slot))
        } returns Result.success(Unit)

        // when
        val actualResult = defaultGithubProfileRepository.saveGithubProfile(githubProfile)

        // then
        assertAll(
            { coVerify(exactly = 1) { localGithubProfileSource.saveGithubProfile(githubProfile) } },
            { coVerify(exactly = 0) { remoteGithubProfileSource.saveGithubProfile(githubProfile) } },
            { assertThat(slot.captured).isEqualTo(githubProfile) },
            { assertThat(actualResult.isSuccess).isTrue() },
        )
    }
}
