package com.tce.teacherapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tce.teacherapp.R
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.TCEService.Companion.BASE_URL
import com.tce.teacherapp.db.AppDatabase
import com.tce.teacherapp.db.AppDatabase.Companion.DATABASE_NAME
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.repository.LoginRepository
import com.tce.teacherapp.repository.LoginRepositoryImpl
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.repository.MainRepositoryImpl
import com.tce.teacherapp.util.PreferenceKeys
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPreferences(
        application: Application
    ): SharedPreferences {
        return application
            .getSharedPreferences(
                PreferenceKeys.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonBuilder: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() // get correct db version if schema changed
            .build()
    }

    @JvmStatic
    @Provides
    fun provideSubjectsDao(db: AppDatabase): SubjectsDao {
        return db.getSubjectDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions
            .placeholderOf(R.drawable.ic_letters_sounds_2)
            .error(R.drawable.ic_letters_sounds_2)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGlideInstance(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide.with(application)
            .setDefaultRequestOptions(requestOptions)
    }

    @JvmStatic
    @Provides
    fun provideTceService(retrofitBuilder: Retrofit.Builder): TCEService {
        return retrofitBuilder
            .build()
            .create(TCEService::class.java)
    }

    @JvmStatic
    @Provides
    fun provideMainRepository(
        subjectDao: SubjectsDao,
        tceService: TCEService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor,
        application: Application
    ): MainRepository {
        return MainRepositoryImpl(
            subjectDao,
            tceService,
            preferences,
            editor,
            application
        )
    }

    @JvmStatic
    @Provides
    fun provideLoginRepository(
        tceService: TCEService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): LoginRepository {
        return LoginRepositoryImpl(
            tceService,
            preferences,
            editor
        )
    }

}