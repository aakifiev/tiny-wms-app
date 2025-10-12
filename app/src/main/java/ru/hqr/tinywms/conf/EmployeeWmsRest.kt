package ru.hqr.tinywms.conf

import ru.hqr.tinywms.client.EmployeeWmsApi
import ru.hqr.tinywms.client.TinyWmsApi

object EmployeeWmsRest {
    val retrofitService: EmployeeWmsApi by lazy {
        retrofit.create(EmployeeWmsApi::class.java)
    }
}