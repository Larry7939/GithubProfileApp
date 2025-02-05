package com.malibin.study.github.presentation.sample

import com.google.common.truth.Truth.assertThat
import com.malibin.study.github.utils.CoroutinesTestRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class TestViewModelTest {

    private lateinit var countMemory: CountMemory
    private lateinit var testViewModel: TestViewModel

    @BeforeEach
    fun setUp() {
        countMemory = mockk(relaxed = true)
        testViewModel = TestViewModel(countMemory)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test1() = runTest {
        //given
        //when

        testViewModel.increase()
        advanceUntilIdle()

        //then
        assertAll(
            { assertThat(testViewModel.count.value).isEqualTo(1) },
            { verify(exactly = 1) { countMemory.save("increase") } }
        )
    }

    companion object {
        @JvmStatic
        @RegisterExtension
        val asdf = CoroutinesTestRule()
    }
}