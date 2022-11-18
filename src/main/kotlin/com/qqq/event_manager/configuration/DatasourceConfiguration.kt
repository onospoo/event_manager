package com.qqq.event_manager.configuration

import com.qqq.event_manager.model.db.EventTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatasourceConfiguration {

    @Bean
    fun database(dataSource: DataSource): Database =
        Database.connect(dataSource)
            .also {
                transaction(db = it) {
                    SchemaUtils.create(EventTable)
                }
            }
}