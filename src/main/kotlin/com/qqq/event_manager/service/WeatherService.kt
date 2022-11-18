package com.qqq.event_manager.service

import com.qqq.event_manager.model.dto.WeatherResult
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class WeatherService(
    @Qualifier("weatherClient")
    private val weatherClient: WebClient
) {

    suspend fun getWeather(
        city: String,
        country: String,
        time: LocalDateTime
    ): String {
        val result = weatherClient.get()
            .uri("forecast?q=$city,$country&units=metric&appid=b0454686b4c0f5459fed65be7ad96ae7")
            .retrieve()
            .awaitBody<WeatherResult>()
        val formattedTime = formatTime(time)
        val forecastForTime = result.list.firstOrNull { it.dtTxt == formattedTime }
        return forecastForTime?.let { "${forecastForTime.main.temp}°C, ${forecastForTime.weather.first().main}" }
            ?: "N/A"
    }

    // Convert time to openweather format like 00:00, 03:00, 06:00 etc.
    private fun formatTime(time: LocalDateTime) =
        time.truncatedTo(ChronoUnit.HOURS).let {
            val roundedTime = it.withHour(it.hour - (it.hour % 3))
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(roundedTime)
        }
}