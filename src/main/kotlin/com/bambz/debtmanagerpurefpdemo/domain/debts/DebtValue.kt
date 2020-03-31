package com.bambz.debtmanagerpurefpdemo.domain.debts

import java.math.BigDecimal
import java.time.LocalDateTime

data class DebtValue(val value: BigDecimal, val time: LocalDateTime)