package com.qqq.event_manager.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WeatherClientConfiguration {

    @Bean
    fun weatherClient(
        @Value("\${weather.base-url}")
        baseUrl: String
    ) =
        WebClient
            .builder()
            .baseUrl(baseUrl)
            .build()
}