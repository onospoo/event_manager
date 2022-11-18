package com.qqq.event_manager.configuration

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.asCoroutineDispatcher
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
class CoroutineConfiguration {

    @Bean(destroyMethod = "close", autowireCandidate = false)
    fun coroutineDispatcher() = Executors.newCachedThreadPool().asCoroutineDispatcher()

    @Bean
    fun coroutineContext() = coroutineDispatcher() + CoroutineExceptionHandler { context, exception ->
        LoggerFactory.getLogger(javaClass)
            .error("Exception occurred while execute coroutine with context = $context", exception)
    }
}
