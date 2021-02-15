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
import com.tce.teacherapp.api.AddCookiesInterceptor
import com.tce.teacherapp.api.ReceivedCookiesInterceptor
import com.tce.teacherapp.api.TCEService
import com.tce.teacherapp.api.TCEService.Companion.BASE_URL
import com.tce.teacherapp.api.TCEService.Companion.BASE_URL_ZL
import com.tce.teacherapp.db.AppDatabase
import com.tce.teacherapp.db.AppDatabase.Companion.DATABASE_NAME
import com.tce.teacherapp.db.dao.SubjectsDao
import com.tce.teacherapp.db.dao.UserDao
import com.tce.teacherapp.repository.LoginRepository
import com.tce.teacherapp.repository.LoginRepositoryImpl
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.repository.MainRepositoryImpl
import com.tce.teacherapp.util.PreferenceKeys
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@FlowPreview
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
    @Named("tceapi")
    fun provideRetrofitBuilder(gsonBuilder: Gson,application: Application): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .client(
                OkHttpClient().newBuilder().readTimeout(2, TimeUnit.MINUTES).connectTimeout(2,
                    TimeUnit.MINUTES)
                .addInterceptor(AddCookiesInterceptor(provideSharedPreferences(application)))
                .addInterceptor(ReceivedCookiesInterceptor(provideSharedPreferences(application))).build())
    }

    @JvmStatic
    @Singleton
    @Provides
    @Named("zlapi")
    fun provideRetrofitBuilderZL(gsonBuilder: Gson,application: Application): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_ZL)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .client(
                OkHttpClient().newBuilder().readTimeout(2, TimeUnit.MINUTES).connectTimeout(2,
                    TimeUnit.MINUTES)
                    .addInterceptor(AddCookiesInterceptor(provideSharedPreferences(application)))
                    .addInterceptor(ReceivedCookiesInterceptor(provideSharedPreferences(application))).build())
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
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.getUserDao()
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
    @Named("tce")
    fun provideTceService(@Named("tceapi") retrofitBuilder: Retrofit.Builder): TCEService {
        return retrofitBuilder
            .build()
            .create(TCEService::class.java)
    }

    @JvmStatic
    @Provides
    @Named("zl")
    fun provideZLService(@Named("zlapi") retrofitBuilder: Retrofit.Builder): TCEService {
        return retrofitBuilder
            .build()
            .create(TCEService::class.java)
    }



    @JvmStatic
    @Provides
    fun provideMainRepository(
        subjectDao: SubjectsDao,
        userDao: UserDao,
        @Named("tce") tceService: TCEService,
        @Named("zl") zlService: TCEService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor,
        application: Application
    ): MainRepository {
        return MainRepositoryImpl(
            subjectDao,
            userDao,
            tceService,
            zlService,
            preferences,
            editor,
            application
        )
    }

    @JvmStatic
    @Provides
    fun provideLoginRepository(
        userDao: UserDao,
        @Named("tce") tceService: TCEService,
        @Named("zl") zlService: TCEService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor,
        application: Application
    ): LoginRepository {
        return LoginRepositoryImpl(
            userDao,
            tceService,
            zlService,
            preferences,
            editor,
            application
        )
    }


}