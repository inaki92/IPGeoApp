package com.inaki.ipgeoapp.usecases

import android.util.Log
import com.google.common.truth.Truth
import com.inaki.ipgeoapp.local.LocalRepository
import com.inaki.ipgeoapp.local.Location
import com.inaki.ipgeoapp.local.mapToLocalLocation
import com.inaki.ipgeoapp.model.IPLocation
import com.inaki.ipgeoapp.rest.NetworkConnection
import com.inaki.ipgeoapp.rest.RemoteLocationRepository
import com.inaki.ipgeoapp.utils.State
import com.inaki.ipgeoapp.utils.TimeUtil
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LocationUseCaseTest {

    private lateinit var testObject: LocationUseCase
    private val mockRemoteRepo = mockk<RemoteLocationRepository>(relaxed = true)
    private val mockLocalRepo = mockk<LocalRepository>(relaxed = true)
    private val mockConnection = mockk<NetworkConnection>(relaxed = true)
    private val mockLocation = mockk<Location>(relaxed = true) {
        every { timeZone } returns "zone"
        every { timeStamp } returns 2L
    }

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 1
        every { Log.d(any(), any()) } returns 1
        mockkObject(TimeUtil::class)
        testObject = LocationUseCase(mockRemoteRepo, mockLocalRepo, mockConnection)
        coEvery { mockLocalRepo.getLocationById("123") } returns null
        coEvery { mockLocalRepo.insertLocation(any()) } returns Unit
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke location by ip address get a success location when absent in database`() {
        val mockIpLocation = mockk<IPLocation>(relaxed = true) {
            every { timezone } returns "EST"
            every { mapToLocalLocation() } returns mockLocation
        }
        every { mockConnection.isNetworkAvailable() } returns true
        coEvery { mockLocalRepo.getLocationById("123") } returns null
        coEvery { mockRemoteRepo.retrieveLocation("123") } returns Response.success(mockIpLocation)

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.SUCCESS::class.java)
        Truth.assertThat((states[0] as State.SUCCESS).data).isNotNull()
        coVerify { mockLocalRepo.insertLocation(any()) }

        job.cancel()
    }

    @Test
    fun `invoke location by ip address get a success location when present in database`() {
        every { mockConnection.isNetworkAvailable() } returns true
        coEvery { mockLocalRepo.getLocationById("123") } returns mockLocation

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.SUCCESS::class.java)
        Truth.assertThat((states[0] as State.SUCCESS).data).isNotNull()

        job.cancel()
    }

    @Test
    fun `invoke location by ip address get a network connection error`() {
        every { mockConnection.isNetworkAvailable() } returns false

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.ERROR::class.java)
        Truth.assertThat((states[0] as State.ERROR).error.message).isEqualTo("No internet connection available")

        job.cancel()
    }

    @Test
    fun `invoke location by ip address get a null body error`() {
        every { mockConnection.isNetworkAvailable() } returns true
        coEvery { mockRemoteRepo.retrieveLocation("123") } returns Response.success(null)

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.ERROR::class.java)
        Truth.assertThat((states[0] as State.ERROR).error.message).isEqualTo("Null body in response")

        job.cancel()
    }

    @Test
    fun `invoke location by ip address get a response error`() {
        every { mockConnection.isNetworkAvailable() } returns true
        coEvery { mockRemoteRepo.retrieveLocation("123") } returns mockk {
            every { isSuccessful } returns false
            every { errorBody() } returns mockk {
                every { string() } returns "error"
            }
        }

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.ERROR::class.java)
        Truth.assertThat((states[0] as State.ERROR).error.message).isEqualTo("error")

        job.cancel()
    }

    @Test
    fun `invoke location by ip address throws any exception error`() {
        every { mockConnection.isNetworkAvailable() } returns true
        coEvery { mockRemoteRepo.retrieveLocation("123") } throws Exception("error")

        val states = mutableListOf<State<Location>>()

        val job = testScope.launch {
            testObject.invoke("123").collect {
                states.add(it)
            }
        }

        Truth.assertThat(states).hasSize(1)
        Truth.assertThat(states[0]).isInstanceOf(State.ERROR::class.java)
        Truth.assertThat((states[0] as State.ERROR).error.message).isEqualTo("error")

        job.cancel()
    }
}