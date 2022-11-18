package com.qqq.event_manager.util

import io.mockk.clearAllMocks
import java.io.Closeable

interface Mockks : Closeable {

    override fun close() {
        clearAllMocks()
    }
}

inline fun <T : Mockks, R> withMockks(receiver: T, block: T.() -> R): R {
    return receiver.use { it.block() }
}
