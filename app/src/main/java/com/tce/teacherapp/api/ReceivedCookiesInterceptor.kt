package com.tce.teacherapp.api

import android.content.SharedPreferences
import com.tce.teacherapp.util.PreferenceKeys
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences MAY VARY.
 *
 *
 * Created by tsuharesu on 4/1/15.
 */
class ReceivedCookiesInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = HashSet<String>()
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            sharedPreferences.edit()
                .putStringSet(PreferenceKeys.PREF_COOKIES, cookies)
                .apply()
        }
        return originalResponse
    }
}