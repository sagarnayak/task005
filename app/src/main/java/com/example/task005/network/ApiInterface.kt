package com.example.task005.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("v3/c52cf4ce-a639-42d7-a606-2c0a8b848536")
    fun getData(): Observable<Response<ResponseBody>>
}