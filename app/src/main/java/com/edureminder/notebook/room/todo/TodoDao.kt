package com.edureminder.notebook.room.todo

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {


    @Query("SELECT * FROM todo_table ORDER BY ID ASC")
    fun getTodosByDate() : Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE ID = :id")
    fun getOneTodoFlow(id: String): Flow<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternal(todo: Todo) // no return type

    suspend fun addTodo(todo: Todo): String {
        insertInternal(todo)
        return todo.id
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM todo_table WHERE prev_todo_id = :prevId LIMIT 1")
    suspend fun findTodoByPrevTodoId(prevId: String): Todo?

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllTodos() : LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllTodosFlow() : Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE ID = :id LIMIT 1")
    fun getOneTodo(id: String): Todo

    @Query("SELECT * FROM todo_table WHERE ID = :id LIMIT 1")
    suspend fun getOneTodoSafe(id: String): Todo?


    @Query("SELECT * FROM todo_table WHERE ID = :id")
    fun getOneTask(id: String): Flow<Todo>

    @Query("SELECT * FROM todo_table WHERE ID = :id LIMIT 1")
    suspend fun getOneTodoSuspend(id: String): Todo?


    @Query("UPDATE todo_table SET status = 'COMPLETED' WHERE id = :id")
    suspend fun markCompleted(id: String)

    @Query("UPDATE todo_table SET flag_id = 0 WHERE id = :id")
    suspend fun clearFlag(id: String)

    @Query("UPDATE todo_table SET flag_id = :flag WHERE id = :id")
    suspend fun updateFlag(id: String, flag: Int)


}