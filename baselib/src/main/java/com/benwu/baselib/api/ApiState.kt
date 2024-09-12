package com.benwu.baselib.api

sealed class ApiState<out T> {
    data class Success<T>(val data: T) : ApiState<T>()

    data class Failure<T>(val throwable: Throwable, val data: T? = null) : ApiState<T>()
}