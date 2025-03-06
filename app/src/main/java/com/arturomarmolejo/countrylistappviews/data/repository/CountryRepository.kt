package com.arturomarmolejo.countrylistappviews.data.repository

import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import retrofit2.Response

/**
 * [CountryRepository] -
 * Defines the methods to get the response from the API asynchronously using suspend functions
 */
interface CountryRepository {
    suspend fun getAllCountries(): Response<List<CountryResponseItem>>
}