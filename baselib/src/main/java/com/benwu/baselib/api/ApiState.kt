package com.benwu.baselib.api

sealed class ApiState<out T> {
    data class Success<out T>(val data: T) : ApiState<T>()

    data class Failure(val throwable: Throwable) : ApiState<Nothing>()
}