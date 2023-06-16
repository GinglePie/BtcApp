package com.neversitup.apptest.btc.core.network.model

import com.neversitup.apptest.btc.core.network.model.util.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCurrencyRate(
    val code: String,
    val symbol: String,
    val rate: String,
    @SerialName(value = "rate_float") val rateFloat: Float ,
    val description: String
)


@Serializable
data class NetworkBpi(
    @SerialName(value = "USD") val usd: NetworkCurrencyRate,
    @SerialName(value = "GBP") val gbp: NetworkCurrencyRate,
    @SerialName(value = "EUR") val eur: NetworkCurrencyRate
)

@Serializable
data class NetworkTimeUpdate(
    @SerialName(value = "updated") val updated: String,
    @Serializable(InstantSerializer::class)
    @SerialName(value = "updatedISO") val updatedISO: Instant,
    @SerialName(value = "updateduk") val updateDuk: String
)

@Serializable
data class PriceResponse(
    val time: NetworkTimeUpdate,
    val disclaimer: String,
    val chartName: String,
    val bpi: NetworkBpi
)