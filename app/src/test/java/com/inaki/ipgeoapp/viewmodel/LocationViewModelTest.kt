package com.inaki.ipgeoapp.viewmodel

import com.google.common.truth.Truth.assertThat
import com.inaki.ipgeoapp.local.Location
import com.inaki.ipgeoapp.usecases.LocationUseCase
import com.inaki.ipgeoapp.utils.State
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {

    private lateinit var testObject: LocationViewModel
    private val mockUseCase = mockk<LocationUseCase>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        testObject = LocationViewModel(mockUseCase)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `get location when selected ip address and valid address`() {
        every { mockUseCase(any()) } returns flowOf(
            State.SUCCESS(mockk(relaxed = true))
        )

        testObject.ipAddress = "24.48.0.124"

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.location.collect {
                states.add(it)
            }
        }

        testObject.findLocation()

        print(states)
        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(State.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(State.SUCCESS::class.java)
        assertThat((states[1] as State.SUCCESS).data).isNotNull()

        job.cancel()
    }

    @Test
    fun `get location when selected ip address and invalid address`() {
        testObject.ipAddress = "a123"

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.location.collect {
                states.add(it)
            }
        }

        testObject.findLocation()

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(State.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(State.ERROR::class.java)
        assertThat((states[1] as State.ERROR).error.localizedMessage).isEqualTo("The IP address is invalid format")

        job.cancel()
    }

    @Test
    fun `get location error when no selected ip address`() {
        testObject.ipAddress = null

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.location.collect {
                states.add(it)
            }
        }

        testObject.findLocation()

        assertThat(states).hasSize(2)
        assertThat(states[0]).isInstanceOf(State.LOADING::class.java)
        assertThat(states[1]).isInstanceOf(State.ERROR::class.java)
        assertThat((states[1] as State.ERROR).error.message).isEqualTo("No IP address entered")

        job.cancel()
    }
}