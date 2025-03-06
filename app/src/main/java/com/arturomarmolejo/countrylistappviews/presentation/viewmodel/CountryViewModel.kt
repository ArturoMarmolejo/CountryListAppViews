package com.arturomarmolejo.countrylistappviews.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.domain.GetAllCountriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CountryViewModel (
    private val getAllCountriesUseCase: GetAllCountriesUseCase
): ViewModel() {

    private val _allCountries: MutableStateFlow<UIState<List<CountryResponseItem>>> = MutableStateFlow(UIState.LOADING)
    val allCountries: StateFlow<UIState<List<CountryResponseItem>>> get() = _allCountries

    init {
        getAllCountries()
    }

    private fun getAllCountries() {
        viewModelScope.launch {
            getAllCountriesUseCase().collect {
                _allCountries.value = it
            }
        }
    }
}