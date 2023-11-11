package com.example.tasky.feature_agenda.domain.use_case

data class Task(
    val createTask: CreateTask,
    val updateTask: UpdateTask,
    val deleteTask: DeleteTask
)
