package com.tce.teacherapp.api

import android.content.SharedPreferences
import android.util.Log
import com.tce.teacherapp.util.PreferenceKeys
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences MAY VARY.
 *
 *
 * Created by tsuharesu on 4/1/15.
 */
class AddCookiesInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        for (cookie in sharedPreferences
            .getStringSet(PreferenceKeys.PREF_COOKIES, emptySet())!!) {
            builder.addHeader("Cookie", cookie)
            Log.v(
                "OkHttp",
                "Adding Header: $cookie"
            ) // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }
        return chain.proceed(builder.build())
    }
}