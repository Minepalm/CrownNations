package com.minepalm.nations.test

import com.minepalm.nations.territory.ProtectionRange
import com.minepalm.nations.utils.ServerLoc
import org.junit.jupiter.api.Test

class ProtectionRangeTest {

    @Test
    fun testProtectionRangeIsIn() {
        val range = ProtectionRange(
            ServerLoc("server", "world", 0, 0, 0),
            ServerLoc("server", "world", 10, 10, 10)
        )
        assert(range.isIn(ServerLoc("server", "world", 5, 5, 5)))
    }

    @Test
    fun printTest() {
        val range = ProtectionRange(
            ServerLoc("server", "world", 0, 0, 0),
            ServerLoc("server", "world", 10, 10, 10)
        )
        println(range)
    }
}