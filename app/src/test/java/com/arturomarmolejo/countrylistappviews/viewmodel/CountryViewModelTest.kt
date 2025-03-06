package com.arturomarmolejo.countrylistappviews.viewmodel

import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.domain.GetAllCountriesUseCase
import com.arturomarmolejo.countrylistappviews.presentation.viewmodel.CountryViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountryViewModelTest {

    private lateinit var countryViewModel: CountryViewModel
    private lateinit var getAllCountriesUseCase: GetAllCountriesUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        getAllCountriesUseCase = mockk<GetAllCountriesUseCase>(relaxed = true)
        countryViewModel = CountryViewModel(getAllCountriesUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllCountries updates allCountries state flow with data from use case`() = runTest {
        //Arrange
        val countryList = listOf(
            CountryResponseItem(name = "Mexico", capital = "Mexico City", region = "North America"),
            CountryResponseItem(name = "US", capital = "Washington D.C.", region = "North America"),
            CountryResponseItem(name = "Canada", capital = "Ottawa", region = "North America")
        )

        val flow = flowOf(
            UIState.LOADING,
            UIState.SUCCESS(countryList)
        )

        coEvery { getAllCountriesUseCase() } returns flow

        //Act
        countryViewModel.getAllCountries()
        countryViewModel.allCountries.drop(1).first()

        assertEquals(UIState.SUCCESS(countryList), countryViewModel.allCountries.value)

    }

    @Test
    fun `getAllCountries handles error state from usecase`() = runTest {
        //Arrange
        val exception = Exception("Network Error")

        val flow = flow { emit(UIState.ERROR(exception))  }

        coEvery { getAllCountriesUseCase() } returns flow

        //Act
        countryViewModel.getAllCountries()
        countryViewModel.allCountries.drop(1).first()

        assertEquals(UIState.ERROR(exception), countryViewModel.allCountries.value)

    }


}