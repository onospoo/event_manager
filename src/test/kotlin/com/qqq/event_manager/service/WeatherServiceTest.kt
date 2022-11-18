package com.qqq.event_manager.service

import com.qqq.event_manager.model.dto.Forecast
import com.qqq.event_manager.model.dto.Temp
import com.qqq.event_manager.model.dto.Weather
import com.qqq.event_manager.model.dto.WeatherResult
import com.qqq.event_manager.util.Mockks
import com.qqq.event_manager.util.withMockks
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

class WeatherServiceTest : FreeSpec({
    "weatherService" - {
        "getWeather" - {
            "success" {
                withMockks(WeatherServiceMocks()) {
                    val eventTime = LocalDateTime.of(2022, 10, 12, 16, 30, 0)
                    val result = weatherService.getWeather("test_city", "test_country", eventTime)

                    result shouldBe "7°C, Sunny"
                }
            }
            "not available" {
                withMockks(WeatherServiceMocks()) {
                    val eventTime = LocalDateTime.of(2023, 10, 12, 16, 30, 0)
                    val result = weatherService.getWeather("test_city", "test_country", eventTime)

                    result shouldBe "N/A"
                }
            }
        }
    }
})

class WeatherServiceMocks : Mockks {
    private val weatherClient = mockk<WebClient>()
    val weatherService = WeatherService(weatherClient)

    val weatherResult = WeatherResult(
        list = listOf(
            Forecast(
                dtTxt = "2022-10-12 15:00:00",
                main = Temp("7"),
                weather = listOf(
                    Weather("Sunny")
                )
            )
        )
    )

    var requestBodyUriSpec = mockk<WebClient.RequestBodyUriSpec>()
    var requestBodySpec = mockk<WebClient.RequestBodySpec>()
    var responseSpec = mockk<WebClient.ResponseSpec>()

    init {
        coEvery { weatherClient.get() } returns requestBodyUriSpec
        coEvery { requestBodyUriSpec.uri(any<String>()) } returns requestBodySpec
        coEvery { requestBodySpec.retrieve() } returns responseSpec
        coEvery {
            responseSpec.bodyToMono(any<ParameterizedTypeReference<*>>())
        } returns weatherResult.toMono()
    }
}