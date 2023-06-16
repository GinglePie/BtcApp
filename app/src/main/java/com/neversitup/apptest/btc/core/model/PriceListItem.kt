package com.neversitup.apptest.btc.core.model

import com.neversitup.apptest.btc.core.network.model.NetworkCurrencyRate
import kotlinx.datetime.Instant

data class PriceListItem(
    val time: Instant,
    val currencyList: List<NetworkCurrencyRate>
)