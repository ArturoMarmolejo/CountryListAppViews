package com.arturomarmolejo.countrylistappviews.domain

import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.data.repository.CountryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetAllCountriesUseCase(
    private val countryRepository: CountryRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<UIState<List<CountryResponseItem>>> = flow {
        emit(UIState.LOADING)
        try {
            val response = countryRepository.getAllCountries()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(UIState.SUCCESS(it))
                } ?: throw Exception("Response from server is null")
            } else throw Exception(response.errorBody().toString())
        } catch (e: Exception) {
            emit(UIState.ERROR(e))
        }
    }.flowOn(coroutineDispatcher)
}