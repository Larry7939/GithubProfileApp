package com.malibin.study.github.presentation.sample

import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class TodoManagerTest {
    @Test
    fun ddd() {
        // given
        val fakeTodoMemory = object :TodoMemory {
            val list = mutableListOf<String>()
            override fun getHistory(): List<String> {
                return list
            }

            override fun create(todo: String) {
                list.add(todo)
            }

            override fun finish(todo: String) {
            }

        }
        val todoManager = TodoManager(fakeTodoMemory)
        todoManager.createTodo("todo1")
        todoManager.createTodo("todo2")
        // when
        val actualHistories = todoManager.getTodoHistories()
        // then
        assertThat(actualHistories.size).isEqualTo(2)
        assertThat(actualHistories).containsExactlyElementsIn(listOf("todo1","todo2")).inOrder()
    }
}