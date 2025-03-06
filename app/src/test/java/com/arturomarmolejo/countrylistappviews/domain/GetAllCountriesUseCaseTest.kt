package com.arturomarmolejo.countrylistappviews.domain

import com.arturomarmolejo.countrylistappviews.core.UIState
import com.arturomarmolejo.countrylistappviews.data.model.CountryResponseItem
import com.arturomarmolejo.countrylistappviews.data.model.Currency
import com.arturomarmolejo.countrylistappviews.data.repository.CountryRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.Result.Companion.success

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllCountriesUseCaseTest {

    private lateinit var getAllCountriesUseCase: GetAllCountriesUseCase
    private lateinit var repository: CountryRepository
    private val dispatcher = UnconfinedTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = mockk<CountryRepository>(relaxed = true)
        getAllCountriesUseCase = GetAllCountriesUseCase(repository, dispatcher)
    }


    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke emits LOADING state first`() = runTest {
        // Arrange
        coEvery { repository.getAllCountries() } returns Response.success(emptyList())

        // Act
        val flow = getAllCountriesUseCase()
        val firstEmission = flow.first()

        // Assert
        assertEquals(UIState.LOADING, firstEmission)
    }

    @Test
    fun `invoke emits SUCCESS state with data when response is successful`() = runTest {
        // Arrange
        val countryList = listOf(
            CountryResponseItem("name1", "capital1", Currency("currency"), "flag1", "code1", ),
            CountryResponseItem("name2", "capital2", Currency("currency"), "flag2", "code2", )
        )
        val response = Response.success(countryList)
        coEvery { repository.getAllCountries() } returns response

        // Act
        val flow = getAllCountriesUseCase()
        val emissions = flow.toList()

        // Assert
        assertEquals(2, emissions.size)
        assertEquals(UIState.LOADING, emissions[0])
        assertEquals(UIState.SUCCESS(countryList), emissions[1])
    }

    @Test
    fun `invoke emits ERROR state when response is NOT successful`() = runTest {
        // Arrange
        val errorMessage = "Error message"
        val response = Response.error<List<CountryResponseItem>>(400, errorMessage.toResponseBody())
        coEvery { repository.getAllCountries() } returns response

        // Act
        val flow = getAllCountriesUseCase()
        val emissions = flow.toList()

        // Assert
        assertEquals(2, emissions.size)
        assertEquals(UIState.LOADING, emissions[0])
        assertTrue(emissions[1] is UIState.ERROR)
        //assertEquals(errorMessage, (emissions[1] as UIState.ERROR).error.message)
    }

    @Test
    fun `invoke emits ERROR state when response body is null`() = runTest {
        // Arrange
        val errorMessage = "Response from server is null"
        coEvery { repository.getAllCountries() } returns Response.success(null)

        // Act
        val flow = getAllCountriesUseCase()
        val emissions = flow.toList()

        // Assert
        assertEquals(2, emissions.size)
        assertEquals(UIState.LOADING, emissions[0])
        assertTrue(emissions[1] is UIState.ERROR)
        assertEquals(errorMessage, (emissions[1] as UIState.ERROR).message.message)
    }

}