package com.malibin.study.github.data.repository

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.data.source.GithubProfileSource
import com.malibin.study.github.domain.profile.GithubProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultGithubProfileRepositoryTest {
    private lateinit var fakeLocalGithubProfileSource: GithubProfileSource
    private lateinit var fakeRemoteGithubProfileSource: GithubProfileSource
    private lateinit var defaultGithubProfileRepository: DefaultGithubProfileRepository
    lateinit var gitHubProfile: GithubProfile

    @BeforeEach
    fun setUp() {
        fakeRemoteGithubProfileSource = mockk()
        fakeLocalGithubProfileSource = mockk()
        defaultGithubProfileRepository = DefaultGithubProfileRepository(
            fakeLocalGithubProfileSource, fakeRemoteGithubProfileSource
        )
    }

    @Test
    fun `GithubProfile이 Local DB에 없으면, Remote의 getGithubProfile()과 Local의 saveGithubProfile이 호출된다`() =
        runBlocking {
            //given
            gitHubProfile = GithubProfile(
                id = 1L,
                userName = "name1",
                avatarUrl = "https://www.linkpicture.com/view.php?img=LPic63df5f35265bb1785508137",
                name = "name1",
                bio = "Hello",
                followersCount = 1,
                followingCount = 1
            )

            coEvery {
                fakeLocalGithubProfileSource.getGithubProfile("name1")
            } returns Result.failure(IllegalArgumentException("cannot find githubProfile of userName(name1)"))
            coEvery {
                fakeRemoteGithubProfileSource.getGithubProfile("name1")
            } returns Result.success(gitHubProfile)
            coEvery {
                fakeLocalGithubProfileSource.saveGithubProfile(gitHubProfile)
            } returns Result.success(Unit)
            //when
            val actualResult = defaultGithubProfileRepository.getGithubProfile("name1")

            //then
            assertAll(
                { coVerify(exactly = 1) {
                    fakeLocalGithubProfileSource.getGithubProfile("name1")
                } },
                { coVerify(exactly = 1) {
                    fakeRemoteGithubProfileSource.getGithubProfile("name1")
                } },
                {
                    coVerify(exactly = 1) {
                        fakeLocalGithubProfileSource.saveGithubProfile(gitHubProfile)
                    }
                },
                { assertThat(actualResult.getOrNull()).isEqualTo(gitHubProfile) },
            )
            confirmVerified(fakeLocalGithubProfileSource, fakeRemoteGithubProfileSource)

        }

    @Test
    fun `userName이 Local DB에 존재하면 GithubProfile을 반환한다`() = runTest {
        //given
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
        //when
        val actualResult = defaultGithubProfileRepository.getGithubProfile("name1")
        //then
        assertAll(
            { assertThat(actualResult.getOrNull()).isEqualTo(gitHubProfile)},
            { coVerify(exactly = 1) { fakeLocalGithubProfileSource.getGithubProfile("name1") } }
        )
    }

    @Test
    fun `GithubProfile을 LocalDB에 저장할 때 LocalGithubProfileSource의 saveGithubProfile 메소드가 호출된다`() =
        runTest {
            //given
            gitHubProfile = GithubProfile(
                id = 1L,
                userName = "name1",
                avatarUrl = "https://www.linkpicture.com/view.php?img=LPic63df5f35265bb1785508137",
                name = "name1",
                bio = "Hello",
                followersCount = 1,
                followingCount = 1
            )

            coEvery { fakeLocalGithubProfileSource.saveGithubProfile(gitHubProfile) } returns Result.success(
                Unit
            )
            //when
            defaultGithubProfileRepository.saveGithubProfile(gitHubProfile)
            //then
            assertAll({
                coVerify(exactly = 1) {
                    fakeLocalGithubProfileSource.saveGithubProfile(
                        gitHubProfile
                    )
                }
                coVerify(exactly = 0) {
                    fakeRemoteGithubProfileSource.saveGithubProfile(
                        gitHubProfile
                    )
                }
            })
        }


}






