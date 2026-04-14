package com.example.cryptomonitor.service

import com.example.cryptomonitor.model.TickerResponse

interface MercadoBitcoinService {

    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}