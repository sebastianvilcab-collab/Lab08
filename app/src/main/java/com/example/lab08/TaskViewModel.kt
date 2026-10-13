package com.example.lab08

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    // Estado para la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // Al inicializar, cargamos las tareas de la base de datos
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    // Función para añadir una nueva tarea con prioridad
    fun addTask(description: String, priority: String = "Media") {
        val newTask = Task(description = description, priority = priority)
        viewModelScope.launch {
            dao.insertTask(newTask)
            loadTasks() // Recargamos la lista
        }
    }

    // Función para alternar el estado de completado de una tarea
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            loadTasks() // Recargamos la lista
        }
    }

    // Función para eliminar una tarea individual
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            loadTasks() // Recargamos la lista
        }
    }

    // Función para eliminar todas las tareas
    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList() // Vaciamos la lista en el estado
        }
    }
}