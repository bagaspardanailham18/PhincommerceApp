package com.bagaspardanailham.core.data

sealed class RoomResult<out R> private constructor() {
    data class Success<out T>(val data: T): RoomResult<T>()
    data class Error(val message: String): RoomResult<Nothing>()
    object Loading : RoomResult<Nothing>()
}