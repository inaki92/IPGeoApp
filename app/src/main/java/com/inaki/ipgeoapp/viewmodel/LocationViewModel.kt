package com.inaki.ipgeoapp.viewmodel

import com.inaki.ipgeoapp.local.Location
import com.inaki.ipgeoapp.usecases.LocationUseCase
import com.inaki.ipgeoapp.utils.BaseViewModel
import com.inaki.ipgeoapp.utils.InvalidIpAddressException
import com.inaki.ipgeoapp.utils.NoIpAddressException
import com.inaki.ipgeoapp.utils.State
import com.inaki.ipgeoapp.utils.checkIPAddressValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val useCase: LocationUseCase
) : BaseViewModel() {

    var ipAddress: String? = null

    private val _location: MutableStateFlow<State<Location>> = MutableStateFlow(State.LOADING)
    val location: StateFlow<State<Location>> get() = _location

    fun findLocation() {
        _location.value = State.LOADING

        ipAddress?.let {
            if (it.checkIPAddressValid()) {
                safeViewModelScope.launch {
                    useCase(it).collect { state ->
                        _location.value = state
                    }
                }
            } else {
                _location.value= State.ERROR(InvalidIpAddressException())
            }
        } ?: let {
            _location.value = State.ERROR(NoIpAddressException())
        }
    }
}