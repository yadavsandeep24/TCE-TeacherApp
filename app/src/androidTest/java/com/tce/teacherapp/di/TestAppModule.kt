package com.tce.teacherapp.di

import android.app.Application
import com.tce.teacherapp.util.JsonUtil
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
object TestAppModule{

    @JvmStatic
    @Singleton
    @Provides
    fun provideJsonUtil(application: Application): JsonUtil {
        return JsonUtil(application)
    }

}














