package com.bambz.debtmanagerpurefpdemo.domain.debts

import java.math.BigDecimal
import java.time.LocalDateTime

data class NewDebtValue(val debtorId: String, val value: BigDecimal, val now: LocalDateTime)