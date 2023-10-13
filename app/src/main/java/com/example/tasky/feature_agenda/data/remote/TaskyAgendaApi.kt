package com.example.tasky.feature_agenda.data.remote

import com.example.tasky.feature_agenda.data.remote.dto.EventDto
import com.example.tasky.feature_agenda.data.remote.dto.ReminderDto
import com.example.tasky.feature_agenda.data.remote.dto.TaskDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface TaskyAgendaApi {

    @GET("/logout")
    suspend fun logout()

    @GET("/agenda")
    suspend fun getAgenda(
        @Query("timezone") timeZone: String,
        @Query("time") time: Long
    ): Response<AgendaResponse>

    @POST("/syncAgenda")
    suspend fun syncAgenda(
        @Body syncIds: SyncAgendaRequest
    )

    @GET("/fullAgenda")
    suspend fun getFullAgenda(): Response<AgendaResponse>

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part("create_event_request") createEventRequest: EventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<EventDto>

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId") eventId: String
    ): Response<EventDto>

    @DELETE("/event")
    suspend fun deleteEvent(
        @Query("eventId") eventId: String
    )

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Part("update_event_request") updateEventRequest: UpdateEventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<EventDto>

    @GET("/attendee")
    suspend fun getAttendee(
        @Query("email") email: String
    ): Response<AttendeeResponse>

    @DELETE("/attendee")
    suspend fun deleteAttendee(
        @Query("eventId") eventId: String
    )

    @POST("/task")
    suspend fun createTask(
        @Body taskDto: TaskDto
    )

    @PUT("/task")
    suspend fun updateTask(
        @Body taskDto: TaskDto
    )

    @GET("/task")
    suspend fun getTask(
        @Query("taskId") taskId: String
    ): Response<TaskDto>

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId") taskId: String
    )

    @POST("/reminder")
    suspend fun createReminder(
        @Body reminderDto: ReminderDto
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminderDto: ReminderDto
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId") reminderId: String
    ): Response<ReminderDto>

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId") reminderId: String
    )
}