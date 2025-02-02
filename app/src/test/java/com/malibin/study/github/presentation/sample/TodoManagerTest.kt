package com.malibin.study.github.presentation.sample

import com.google.common.truth.Truth.assertThat
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.jupiter.api.assertAll

internal class TodoManagerTest {
    @Test
    fun `익명 클래스 및 Mockk를 활용한 화이트 박스 테스트`() {
        // given
//      1. 익명 클래스를 이용한 TodoMemory 구현
//        val fakeTodoMemory = object :TodoMemory {
//            val list = mutableListOf<String>()
//            override fun getHistory(): List<String> {
//                return list
//            }
//
//            override fun create(todo: String) {
//                list.add(todo)
//            }
//
//            override fun finish(todo: String) {
//            }
//
//        }
//        todoManager.createTodo("todo1")
//        todoManager.createTodo("todo2")

//      2. mockk를 이용한 TodoMemory 구현
        // given
        val fakeTodoMemory = mockk<TodoMemory>(relaxed = true)
        every { fakeTodoMemory.getHistory() } returns listOf("todo1", "todo2")
        // just Runs 혹은 relaxed 옵션을 활성화 해야한다.
//        every { fakeTodoMemory.create("todo1") } just Runs
//        every { fakeTodoMemory.create("todo2") } just Runs
        val todoManager = TodoManager(fakeTodoMemory)

        // when
        val actualHistories = todoManager.getTodoHistories()
        todoManager.createTodo("todo1")
        todoManager.createTodo("todo2")

        // then
        assertAll(
            { assertThat(actualHistories.size).isEqualTo(2) },
            { assertThat(actualHistories).containsExactlyElementsIn(listOf("todo1", "todo2")).inOrder() },
            { assertThat(todoManager.getTodos()).containsExactlyElementsIn(listOf("todo1", "todo2")).inOrder() },
            { verify(exactly = 2) { fakeTodoMemory.create(any()) } },
            { verify(atLeast = 1) { fakeTodoMemory.create(any()) } }
        )
    }
}