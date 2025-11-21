package com.edureminder.easynotes.room.todo

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edureminder.easynotes.notification.cancelNotification
import com.edureminder.easynotes.notification.scheduleOrUpdateNotification
import com.edureminder.easynotes.utils.generateUniqueId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import com.edureminder.easynotes.R


@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
) : ViewModel() {
    val todos: Flow<List<Todo>> = repository.getAllTodosFlow()

    val isAnimationPlayingState = mutableStateOf(false)
    private var mediaPlayer : MediaPlayer? = null


    fun getTodosByDate(): Flow<List<Todo>> {
        return repository.getTodosByDate()
    }
    fun insertTodo(todo: Todo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTodo(todo)
        }
    }
    fun updateTodo(todo: Todo){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodo(todo)
        }
    }
    fun deleteTodo(todo: Todo, onDeleted: (String) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val deletedId = repository.deleteTodo(todo)
            withContext(Dispatchers.Main) {
                onDeleted(deletedId)
            }
        }
    }
    fun getOneTask(id: String): Flow<Todo> {
        return repository.getOneTask(id)
    }

    fun getOneTodo(id: String): Todo {
        return repository.getOneTodo(id)
    }


    fun insertTodoAndGet(todo: Todo, onInserted: (Todo) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertTodo(todo)
            todo.id = id
            onInserted(todo)
        }
    }

    fun updateTodoAndGet(id: String, onUpdated: (Todo) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markCompleted(id)
            val todo = repository.getOneTodo(id)
            onUpdated(todo)
        }
    }

    fun playCompletedSound(context: Context){
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context,R.raw.completed)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    fun getOneTodo(id: String, onResult: (Todo?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = repository.getOneTodoSuspend(id)
            withContext(Dispatchers.Main) {
                onResult(todo)
            }
        }
    }

    private var lastDeletedTodo = Todo()

    fun setLastDeletedTodo(todo: Todo){
        lastDeletedTodo = todo
    }

    fun getLastDeletedTodo():Todo{
        return lastDeletedTodo
    }

    fun resetLastDeletedTodo(){
        lastDeletedTodo = Todo()
    }

    fun playDeletedSound(context: Context) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.deleted)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun togglePendingOrCompleted(
        todoId: String,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = repository.getOneTodo(todoId)

            if (todo.status == TodoStatus.PENDING) {
                repository.updateTodo(todo.copy(status = TodoStatus.COMPLETED))
                cancelNotification(context, todo)

                // Handle repeating todos
                val repeatDays = todo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }.ifEmpty { emptyList() }
                if (repeatDays.isNotEmpty()) {
                    val currentDate = LocalDate.parse(todo.date)
                    var nextDate = currentDate.plusDays(1)

                    while (true) {
                        val dayOfWeek = nextDate.dayOfWeek.value % 7 + 1
                        if (repeatDays.contains(dayOfWeek)) break
                        nextDate = nextDate.plusDays(1)
                    }

                    val newTodo = todo.copy(
                        id = generateUniqueId(),
                        date = nextDate.toString(),
                        status = TodoStatus.PENDING,
                        prevTodoId = todo.id
                    )

                    if (todo.time.isNotEmpty()) {
                        val newId = repository.insertTodo(newTodo)
                        scheduleOrUpdateNotification(context, newTodo)
                    }
                }
            } else if (todo.status == TodoStatus.COMPLETED) {
                repository.updateTodo(todo.copy(status = TodoStatus.PENDING))
                scheduleOrUpdateNotification(context, todo)
            }
        }
    }
    fun togglePendingOrCancelled(
        todoId: String,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = repository.getOneTodo(todoId)

            when (todo.status) {
                TodoStatus.PENDING -> {
                    repository.updateTodo(todo.copy(status = TodoStatus.CANCELLED))
                    cancelNotification(context, todo)

                    // Handle repeating todos
                    val repeatDays = todo.repeatDays.split(",").mapNotNull { it.toIntOrNull() }.ifEmpty { emptyList() }
                    if (repeatDays.isNotEmpty()) {
                        val currentDate = LocalDate.parse(todo.date)
                        var nextDate = currentDate.plusDays(1)

                        while (true) {
                            val dayOfWeek = nextDate.dayOfWeek.value % 7 + 1
                            if (repeatDays.contains(dayOfWeek)) break
                            nextDate = nextDate.plusDays(1)
                        }

                        val newTodo = todo.copy(
                            date = nextDate.toString(),
                            status = TodoStatus.PENDING,
                            prevTodoId = todo.id
                        )

                        if (todo.time.isNotEmpty()) {
                            val newId = repository.insertTodo(newTodo)
                            scheduleOrUpdateNotification(context, newTodo)
                        }
                    }
                }
                TodoStatus.CANCELLED -> {
                    repository.updateTodo(todo.copy(status = TodoStatus.PENDING))
                    scheduleOrUpdateNotification(context, todo)
                }
                else -> {
                    Log.d("TodoToggle", "Status is not PENDING or CANCELLED, no action taken")
                }
            }
        }
    }

    fun clearFlag(todoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearFlag(todoId)
        }
    }
    fun updateFlag(todoId: String, newFlag: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFlag(todoId, newFlag)
        }
    }
}