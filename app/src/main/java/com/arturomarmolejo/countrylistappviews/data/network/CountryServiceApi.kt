package com.arturomarmolejo.countrylistappviews.data.network

import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import retrofit2.Response
import retrofit2.http.GET

/**
 * [CountryServiceApi] -
 * Defines methods to call the API with Retrofit
 */
interface CountryServiceApi {

    /**
     * [getAllCountries] -
     * Gets List of Countries from the API asynchronously using a suspend function,
     * to be later collected by a coroutine scope
     * @return Response<List<CountryResponseItem>
     */
    @GET(ID + ENDPOINT + COUNTRIES)
    suspend fun getAllCountries(): Response<List<CountryResponseItem>>

    companion object {
        //https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json
        const val BASE_URL = "https://gist.githubusercontent.com/peymano-wmt/"
        const val ID =  "32dcb892b06648910ddd40406e37fdab/"
        const val ENDPOINT = "raw/db25946fd77c5873b0303b858e861ce724e0dcd0/"
        const val COUNTRIES = "countries.json"
    }
}