package com.inaki.ipgeoapp.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

private const val TAG = "BaseViewModel"

open class BaseViewModel(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val job: Job = SupervisorJob(),
    private val errorHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, "ViewModel: Error in safe coroutine scope $coroutineContext", throwable)
    }
) : ViewModel() {

    protected val safeViewModelScope by lazy {
        viewModelScope + ioDispatcher + job + errorHandler
    }
}