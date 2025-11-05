package ru.hqr.tinywms.dto.client

import java.math.BigDecimal

data class StockInfoRequest(
    val barcode: String,
    val quantity: BigDecimal,
    val measureUnit: String)
