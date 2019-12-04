package com.bambz.debtmanagerpurefpdemo.domain.kernel

import java.time.LocalDateTime

class TimeServiceDefault : TimeService {
    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}