package com.example.tasky.feature_agenda.data.remote

import com.example.tasky.feature_agenda.data.remote.dto.AgendaResponse
import com.example.tasky.feature_agenda.data.remote.dto.AttendeeResponse
import com.example.tasky.feature_agenda.domain.model.Event
import com.example.tasky.feature_agenda.data.remote.dto.EventRequest
import com.example.tasky.feature_agenda.data.remote.dto.SyncAgendaRequest
import com.example.tasky.feature_agenda.data.remote.dto.UpdateEventRequest
import com.example.tasky.feature_agenda.domain.model.Reminder
import com.example.tasky.feature_agenda.domain.model.Task
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
    suspend fun getFullAgenda(
        @Body agenda: AgendaResponse
    )

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part("create_event_request") createEventRequest: EventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<Event>

    @GET("/event")
    suspend fun getEvent(
        @Query("eventId") eventId: String
    ): Response<Event>

    @DELETE("/event")
    suspend fun deleteEvent(
        @Query("eventId") eventId: String
    )

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Part("update_event_request") updateEventRequest: UpdateEventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<Event>

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
        @Body task: Task
    )

    @PUT("/task")
    suspend fun updateTask(
        @Body task: Task
    )

    @GET("/task")
    suspend fun getTask(
        @Query("taskId") taskId: String
    ): Response<Task>

    @DELETE("/task")
    suspend fun deleteTask(
        @Query("taskId") taskId: String
    )

    @POST("/reminder")
    suspend fun createReminder(
        @Body reminder: Reminder
    )

    @PUT("/reminder")
    suspend fun updateReminder(
        @Body reminder: Reminder
    )

    @GET("/reminder")
    suspend fun getReminder(
        @Query("reminderId") reminderId: String
    ): Response<Reminder>

    @DELETE("/reminder")
    suspend fun deleteReminder(
        @Query("reminderId") reminderId: String
    )
}