package com.arturomarmolejo.countrylistappviews.data.repository.impl

import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.data.network.CountryServiceApi
import com.arturomarmolejo.countrylistappviews.data.repository.CountryRepository
import retrofit2.Response

/**
 * [CountryRepositoryImpl] -
 * Implementation of [CountryRepository] interface
 * @param countryServiceApi defines the API interface to be called in order to get the response
 * since it is an operation to be performed in the background
 */
class CountryRepositoryImpl(
    private val countryServiceApi: CountryServiceApi,
): CountryRepository {
    /**
     * [getAllCountries] -
     * Gets List of Countries information from the API asynchronously using a suspend function,
     */
    override suspend fun getAllCountries(): Response<List<CountryResponseItem>> =
        countryServiceApi.getAllCountries()
}