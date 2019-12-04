package com.bambz.debtmanagerpurefpdemo.domain.debts.api

import io.vavr.collection.List

data class DebtsDto(val userId: String, val list: List<DebtResultDto>)

data class DebtResultDto(val fromUser: String, val resultAmount: String)