package ru.hqr.tinywms.dto.client

import java.math.BigDecimal

data class StockListInfo(
    val barcode: String,
    val addressId: String,
    val quantity: BigDecimal,
    val measureUnit: String)
