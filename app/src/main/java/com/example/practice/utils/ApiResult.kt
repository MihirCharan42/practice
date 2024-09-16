package com.example.practice.utils

sealed class ApiResult<T>(val data: T? = null, val message: String? = null, val error: ApiError? = null) {
    class Success<T>(data: T?): ApiResult<T>(data)
    class Loading<T>(): ApiResult<T>()
    class Error<T>(error: ApiError? = null, message: String?): ApiResult<T>(message = message, error = error)
}