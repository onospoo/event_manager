package com.qqq.event_manager.model.dto

import com.qqq.event_manager.model.db.EventEntity
import java.time.LocalDateTime

data class Event(
    val id: String,
    val name: String,
    val time: LocalDateTime,
    val city: String,
    val country: String,
    val guestList: List<String>,
    val weather: String,
    val createdAt: LocalDateTime
)

data class GetEventResponse(
    val event: Event
)

data class GetEventsListResponse(
    val items: List<Event>,
    val nextPage: Long? = null
)

fun EventEntity.toDto() = Event(
    id = this.id.toString(),
    name = this.name,
    time = this.time,
    city = this.city,
    country = this.country,
    guestList = this.guestList.toList(),
    weather = this.weather,
    createdAt = this.createdAt
)