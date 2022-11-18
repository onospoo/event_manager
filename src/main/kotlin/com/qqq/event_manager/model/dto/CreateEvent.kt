package com.qqq.event_manager.model.dto

import java.time.LocalDateTime

data class CreateEventRequest(
    val name: String,
    val time: LocalDateTime,
    val city: String,
    val country: String,
    val guestList: List<String>
)

data class CreateEventResponse(val id: String)

object DeleteEventResponse