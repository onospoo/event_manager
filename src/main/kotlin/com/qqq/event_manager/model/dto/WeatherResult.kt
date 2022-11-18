package com.qqq.event_manager.model.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResult(
    @JsonProperty("list")
    val list: List<Forecast>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Forecast(
    @JsonProperty("dt_txt")
    val dtTxt: String,
    @JsonProperty("main")
    val main: Temp,
    @JsonProperty("weather")
    val weather: List<Weather>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Temp(
    @JsonProperty("temp")
    val temp: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(
    @JsonProperty("main")
    val main: String
)