package com.rumeysa.ratexchange.service

import com.rumeysa.ratexchange.model.Currency
import com.rumeysa.ratexchange.model.SymbolsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyAPI {

    companion object {
        const val ACCESS_KEY = "d3a6c2368bf261a6217a77e1127d065d"
    }

    @GET("latest")
    fun getLatestRates(
        @Query("access_key") accessKey: String =  ACCESS_KEY,
    ): Call<Currency>

    @GET("fluctuation")
    fun getFluctuation(
        @Query("access_key") accessKey: String = ACCESS_KEY,
        @Query("base") base: String,
        @Query("symbols") symbols: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<Currency>

    @GET("symbols")
    fun getSymbols(
        @Query("access_key") accessKey: String = ACCESS_KEY
    ): Call<SymbolsResponse>
}