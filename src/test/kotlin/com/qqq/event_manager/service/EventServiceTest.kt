package com.qqq.event_manager.service

import com.qqq.event_manager.model.db.EventEntity
import com.qqq.event_manager.model.db.EventTable
import com.qqq.event_manager.model.dto.CreateEventRequest
import com.qqq.event_manager.model.dto.Event
import com.qqq.event_manager.service.db.EventManager
import com.qqq.event_manager.util.Mockks
import com.qqq.event_manager.util.withMockks
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.UUID

class EventServiceTest : FreeSpec({
    "EventService" - {
        "createEvent" - {
            "success" {
                withMockks(EventServiceMocks()) {
                    val request = CreateEventRequest(
                        name = "test_name",
                        time = LocalDateTime.MAX,
                        city = "test_city",
                        country = "test_country",
                        guestList = listOf("test_guest")
                    )

                    every { EventManager.createEvent(any(), any()) } returns eventId

                    val response = eventService.createEvent(request)
                    response.id shouldBe eventId.toString()
                }
            }
            "time of event has passed" {
                withMockks(EventServiceMocks()) {
                    val request = CreateEventRequest(
                        name = "test_name",
                        time = LocalDateTime.MIN,
                        city = "test_city",
                        country = "test_country",
                        guestList = listOf("test_guest")
                    )

                    val exception = shouldThrow<IllegalArgumentException> {
                        eventService.createEvent(request)
                    }

                    exception.message shouldBe "Time of event has passed"
                }
            }
        }
        "getEvent" - {
            "success" {
                withMockks(EventServiceMocks()) {
                    every { EventManager.getEventById(eventId) } returns eventEntity

                    val response = eventService.getEvent(eventId)
                    response.event shouldBe event
                }
            }
            "does not exist" {
                withMockks(EventServiceMocks()) {
                    every { EventManager.getEventById(eventId) } returns null

                    val exception = shouldThrow<NoSuchElementException> {
                        eventService.getEvent(eventId)
                    }

                    exception.message shouldBe "Event does not exist"
                }
            }
        }
        "getEventsList" - {
            "all events" {
                withMockks(EventServiceMocks()) {
                    every { EventManager.getEventsList(null, any(), any()) } returns SizedCollection(eventEntity)

                    val response = eventService.getEventsList(false, 1, 20)
                    response.items.first() shouldBe event
                }
            }
            "onlyAvailable" {
                withMockks(EventServiceMocks()) {
                    mockkStatic("java.time.LocalDateTime")
                    every { LocalDateTime.now() } returns LocalDateTime.MIN
                    every { EventManager.getEventsList(LocalDateTime.MIN, any(), any()) } returns SizedCollection(
                        eventEntity
                    )

                    val response = eventService.getEventsList(true, 1, 20)
                    response.items.first() shouldBe event
                }
            }
        }
        "deleteEvent" - {
            "success" {
                withMockks(EventServiceMocks()) {
                    every { EventManager.deleteEvent(any()) } returns 1

                    eventService.deleteEvent(eventId)
                    verify { EventManager.deleteEvent(eventId) }
                }
            }
            "does not exist" {
                withMockks(EventServiceMocks()) {
                    every { EventManager.deleteEvent(any()) } returns 0

                    val exception = shouldThrow<NoSuchElementException> {
                        eventService.deleteEvent(eventId)
                    }

                    exception.message shouldBe "Event does not exist"
                }
            }
        }
    }
})

class EventServiceMocks : Mockks {
    private val database = mockk<Database>()
    private val weatherService = mockk<WeatherService>()
    val eventService = EventService(
        database = database,
        weatherService = weatherService
    )

    private val slot = slot<suspend Transaction.() -> Any>()
    private val transactionMock = mockk<Transaction>()

    val eventEntity = mockk<EventEntity>()

    private val weather = "7.4°C, Sunny"

    val eventId = UUID.randomUUID()
    val event = Event(
        id = eventId.toString(),
        name = "test_name",
        time = LocalDateTime.MAX,
        city = "test_city",
        country = "test_country",
        guestList = listOf("test_guest"),
        weather = weather,
        createdAt = LocalDateTime.MAX
    )

    init {

        mockkStatic("org.jetbrains.exposed.sql.transactions.experimental.SuspendedKt")
        coEvery { newSuspendedTransaction(any(), any(), any(), capture(slot)) } coAnswers {
            val statement = slot.captured
            statement(transactionMock)
        }

        coEvery {
            weatherService.getWeather(any(), any(), any())
        } returns weather

        mockkObject(EventManager)
        every { eventEntity.id } returns EntityID(eventId, EventTable)
        every { eventEntity.name } returns event.name
        every { eventEntity.time } returns event.time
        every { eventEntity.city } returns event.city
        every { eventEntity.country } returns event.country
        every { eventEntity.guestList } returns event.guestList.toTypedArray()
        every { eventEntity.weather } returns event.weather
        every { eventEntity.createdAt } returns event.createdAt
    }
}