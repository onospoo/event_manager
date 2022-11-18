package com.qqq.event_manager.service

import com.qqq.event_manager.model.dto.CreateEventRequest
import com.qqq.event_manager.model.dto.CreateEventResponse
import com.qqq.event_manager.model.dto.GetEventResponse
import com.qqq.event_manager.model.dto.GetEventsListResponse
import com.qqq.event_manager.model.dto.toDto
import com.qqq.event_manager.service.db.EventManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class EventService(
    private val database: Database,
    private val weatherService: WeatherService
) {

    private companion object {
        val log = LoggerFactory.getLogger(javaClass)
    }

    suspend fun createEvent(request: CreateEventRequest): CreateEventResponse {
        if (request.time < LocalDateTime.now()) {
            throw IllegalArgumentException("Time of event has passed")
        }
        val weather = weatherService.getWeather(request.city, request.country, request.time)
        return newSuspendedTransaction(db = database) {
            val eventId = EventManager.createEvent(request, weather)
            CreateEventResponse(eventId.toString())
        }
    }

    suspend fun getEvent(eventId: UUID): GetEventResponse =
        newSuspendedTransaction(db = database) {
            val event = EventManager.getEventById(eventId)?.toDto()
                ?: throw NoSuchElementException("Event does not exist").also {
                    log.error("Event $eventId does not exist")
                }
            GetEventResponse(event)
        }

    suspend fun getEventsList(onlyAvailable: Boolean, count: Int, page: Long): GetEventsListResponse {
        val lowerDate = if (onlyAvailable) LocalDateTime.now() else null
        return newSuspendedTransaction(db = database) {
            val eventsList = EventManager.getEventsList(lowerDate, count + 1, page)
            val nextPage = if (eventsList.count() > count) {
                page + 1
            } else {
                null
            }
            GetEventsListResponse(
                items = eventsList.take(count).map { it.toDto() },
                nextPage = nextPage
            )
        }
    }

    suspend fun deleteEvent(eventId: UUID) {
        return newSuspendedTransaction(db = database) {
            val isDeleted = EventManager.deleteEvent(eventId) > 0
            if (!isDeleted) {
                throw NoSuchElementException("Event does not exist").also {
                    log.error("Event $eventId does not exist")
                }
            }
        }
    }
}