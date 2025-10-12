package ru.hqr.tinywms.conf

import ru.hqr.tinywms.client.TinyWmsApi

object TinyWmsRest {
    val retrofitService: TinyWmsApi by lazy {
        retrofit.create(TinyWmsApi::class.java)
    }
}