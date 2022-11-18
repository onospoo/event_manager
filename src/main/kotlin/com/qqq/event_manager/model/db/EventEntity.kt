package com.qqq.event_manager.model.db

import com.qqq.event_manager.util.array
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

object EventTable : UUIDTable("events") {
    val name = varchar("name", 255)
    val time = datetime("time")
    val city = varchar("city", 255)
    val country = varchar("country", 255)
    val guestList = array<String>("guest_list", VarCharColumnType())
    val weather = varchar("weather", 255)
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now(ZoneOffset.UTC) }
}

class EventEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EventEntity>(EventTable)

    var name by EventTable.name
    var time by EventTable.time
    var city by EventTable.city
    var country by EventTable.country
    var guestList by EventTable.guestList
    var weather by EventTable.weather
    var createdAt by EventTable.createdAt
}
