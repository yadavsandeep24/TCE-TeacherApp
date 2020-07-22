package com.tce.teacherapp.util

interface Constants {

    companion object {
        const val TAG = "SAN" // Tag for logs
        const val NETWORK_TIMEOUT = 6000L
        const val CACHE_TIMEOUT = 2000L
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val DEFAULT_KEY_NAME = "tce_default_key"
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }
}