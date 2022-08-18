package com.example.task005.di

import com.example.task005.network.ApiInterface
import com.example.task005.utils.logutil.LogUtil
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkModule(logUtil: LogUtil, private val baseUrl: String) {

    var apiInterface: ApiInterface

    init {
        apiInterface = getApiInterface(
            getRetrofit(
                getOkHttpClient(
                    getHttpLoggingInterceptor(
                        logUtil
                    )
                )
            )
        )
    }

    private fun getHttpLoggingInterceptor(logUtil: LogUtil): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    logUtil.logV(message)
                }
            }
        )
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    private fun getOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

    private fun getRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

    private fun getApiInterface(retrofit: Retrofit) = retrofit.create(ApiInterface::class.java)
}