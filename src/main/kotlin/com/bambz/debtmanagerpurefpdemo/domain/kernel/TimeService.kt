package com.bambz.debtmanagerpurefpdemo.domain.kernel

import java.time.LocalDateTime

interface TimeService {

    fun now(): LocalDateTime

}

