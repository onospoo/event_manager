package com.qqq.event_manager.controller

import com.qqq.event_manager.model.dto.CreateEventRequest
import com.qqq.event_manager.model.dto.CreateEventResponse
import com.qqq.event_manager.model.dto.DeleteEventResponse
import com.qqq.event_manager.model.dto.GetEventResponse
import com.qqq.event_manager.model.dto.GetEventsListResponse
import com.qqq.event_manager.service.EventService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.withContext
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.constraints.Min
import kotlin.coroutines.CoroutineContext

@RestController
@RequestMapping("/api/v1/event")
class EventController(
    private val coroutineDispatcher: CoroutineContext,
    private val eventService: EventService
) {

    @PostMapping("/")
    @Operation(
        summary = "Create event",
        responses = []
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CreateEventResponse::class)
                    )
                ]
            )
        ]
    )
    suspend fun createEvent(
        @RequestBody
        request: CreateEventRequest
    ): CreateEventResponse = withContext(coroutineDispatcher) {
        eventService.createEvent(request)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get event by id",
        responses = []
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetEventResponse::class)
                    )
                ]
            )
        ]
    )
    suspend fun getEvent(
        @PathVariable("id", required = true)
        id: UUID
    ): GetEventResponse = withContext(coroutineDispatcher) {
        eventService.getEvent(id)
    }

    @GetMapping("/")
    @Operation(
        summary = "Get events list",
        responses = []
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GetEventsListResponse::class)
                    )
                ]
            )
        ]
    )
    suspend fun getEventsList(
        @RequestParam("onlyAvailable", required = false, defaultValue = "false")
        onlyAvailable: Boolean,
        @RequestParam("count", required = false, defaultValue = "20")
        @Min(1)
        count: Int,
        @RequestParam("page", required = false, defaultValue = "1")
        @Min(1)
        page: Long
    ): GetEventsListResponse = withContext(coroutineDispatcher) {
        eventService.getEventsList(onlyAvailable, count, page)
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete event by id",
        responses = []
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DeleteEventResponse::class)
                    )
                ]
            )
        ]
    )
    suspend fun deleteEvent(
        @PathVariable("id", required = true)
        id: UUID
    ): DeleteEventResponse = withContext(coroutineDispatcher) {
        eventService.deleteEvent(id)
        DeleteEventResponse
    }
}