package com.bambz.debtmanagerpurefpdemo.domain.debts

import java.math.BigDecimal
import java.time.LocalDateTime

data class DebtPosition(val value: BigDecimal, val date: LocalDateTime)