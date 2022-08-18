package com.example.task005.utils

import okhttp3.ResponseBody

interface SuperRepositoryCallbackForRawResponse {
    fun giveRawResponse(response: ResponseBody?) {}
}