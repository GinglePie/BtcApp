package com.neversitup.apptest.btc.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.neversitup.apptest.btc.BuildConfig
import com.neversitup.apptest.btc.core.network.model.PriceResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET




interface BtcApiService {
    @GET(value = "bpi/currentprice.json")
    suspend fun getBtcPrice(): PriceResponse

}

object BtcApi {
    private const val BASE_URL = BuildConfig.API_URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()
    }

    val retrofitService: BtcApiService by lazy {
        retrofit.create(BtcApiService::class.java)
    }
}
