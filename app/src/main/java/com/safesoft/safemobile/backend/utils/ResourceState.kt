package com.safesoft.safemobile.backend.utils

sealed class ResourceState {
    object LOADING : ResourceState()

    object SUCCESS : ResourceState()

    object ERROR : ResourceState()
}

data class Resource<out T>(
    val state: ResourceState,
    val data: T? = null,
    val message: String? = null,
    val messageId: Int? = null,
    val exception: Throwable? = null
)