package com.example.task005.di

import android.content.Context
import android.content.SharedPreferences
import com.example.task005.contracts.Repository
import com.example.task005.core.KeywordsAndConstants.LOG_TAG
import com.example.task005.core.KeywordsAndConstants.SHOW_LOG
import com.example.task005.utils.RecyclerViewUtil
import com.example.task005.utils.ResourcesMaster
import com.example.task005.utils.logutil.LogUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesLogUtil() = LogUtil(
        LogUtil.Builder()
            .setCustomLogTag(LOG_TAG)
            .setShouldHideLog(SHOW_LOG)
    )

    @Singleton
    @Provides
    fun providesSharedPref(@ApplicationContext context: Context) = SharedPrefModule(context).pref

    @Singleton
    @Provides
    fun providesResourcesMaster(@ApplicationContext context: Context) = ResourcesMaster(context)

    @Singleton
    @Provides
    fun providesRecyclerViewUtil() =
        RecyclerViewUtil()

    @Singleton
    @Provides
    fun providesRepository(
        pref: SharedPreferences,
        logUtil: LogUtil,
        resourcesMaster: ResourcesMaster,
    ): Repository {
        return com.example.task005.network.Repository(
            pref,
            logUtil,
            resourcesMaster
        )
    }
}