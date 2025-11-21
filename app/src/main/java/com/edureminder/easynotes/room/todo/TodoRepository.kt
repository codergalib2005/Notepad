package com.edureminder.easynotes.room.todo

import android.util.Log
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo): String {
        return todoDao.addTodo(todo)
    }

    suspend fun updateTodo(todo: Todo){
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: Todo): String {
        todoDao.deleteTodo(todo)
        return todo.id // return ID
    }


    suspend fun findTodoByPrevTodoId(prevId: String): Todo? {
        return todoDao.findTodoByPrevTodoId(prevId)
    }


    fun getAllTodosFlow() : Flow<List<Todo>> = todoDao.getAllTodosFlow()
    fun getTodosByDate() : Flow<List<Todo>> = todoDao.getTodosByDate()

    fun getOneTodo(id: String) : Todo = todoDao.getOneTodo(id)
    // Fetch a single note by its ID
    fun getOneTask(id: String): Flow<Todo> {
        return todoDao.getOneTask(id)
    }
    suspend fun getOneTodoSuspend(id: String): Todo? = todoDao.getOneTodoSuspend(id)

    suspend fun markCompleted(id: String){
        todoDao.markCompleted(id)
    }
    suspend fun clearFlag(id: String) {
        todoDao.clearFlag(id)
    }

    suspend fun updateFlag(id: String, flag: Int) {
        todoDao.updateFlag(id, flag)
    }


}