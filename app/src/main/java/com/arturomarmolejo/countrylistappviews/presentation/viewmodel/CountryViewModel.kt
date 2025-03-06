package com.arturomarmolejo.countrylistappviews.presentation.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.domain.GetAllCountriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * [CountryViewModel] -
 * Defines the ViewModel for the application
 * Contains the information that may be shared between different composable functions
 * @param getAllCountriesUseCase defines the repository to be used by the ViewModel in order to retrieve
 * data from the data layer
 */
class CountryViewModel (
    private val getAllCountriesUseCase: GetAllCountriesUseCase
): ViewModel() {

    private val _allCountries: MutableStateFlow<UIState<List<CountryResponseItem>>> = MutableStateFlow(UIState.LOADING)
    val allCountries: StateFlow<UIState<List<CountryResponseItem>>> get() = _allCountries

    init {
        getAllCountries()
    }

    /**
     * [getAllCountries] -
     * Retrieves the API data stream from the usecase and saves it in a mutable state to be used
     * by the views in this layer
     */
    @VisibleForTesting
    internal fun getAllCountries() {
        viewModelScope.launch {
            getAllCountriesUseCase().collect {
                _allCountries.value = it
            }
        }
    }
}