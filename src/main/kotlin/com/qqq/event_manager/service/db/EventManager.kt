package com.qqq.event_manager.service.db

import com.qqq.event_manager.model.db.EventEntity
import com.qqq.event_manager.model.db.EventTable
import com.qqq.event_manager.model.dto.CreateEventRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

object EventManager {
    fun createEvent(request: CreateEventRequest, weatherDay: String) =
        EventTable.insertAndGetId { event ->
            event[name] = request.name
            event[time] = request.time
            event[city] = request.city
            event[country] = request.country
            event[guestList] = request.guestList.toTypedArray()
            event[weather] = weatherDay
        }.value

    fun getEventById(id: UUID) = EventEntity
        .find {
            (EventTable.id eq id) and
                (EventTable.isDeleted eq false)
        }.firstOrNull()

    fun getEventsList(lowerDate: LocalDateTime?, count: Int, page: Long) =
        EventEntity.find {
            (EventTable.isDeleted eq false).and(
                lowerDate?.let {
                    EventTable.time greaterEq LocalDateTime.now()
                } ?: Op.TRUE)
        }.orderBy(EventTable.time to SortOrder.ASC)
            .limit(count, (page - 1) * count)

    fun deleteEvent(id: UUID) =
        EventTable.update({ EventTable.id eq id }) {
            it[isDeleted] = true
        }
}