package com.tce.teacherapp.api.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.tce.teacherapp.TCEApplication
import com.tce.teacherapp.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class AlarmReceiver :
    BroadcastReceiver(), CoroutineScope {

    @Inject
    lateinit var loginRepository: LoginRepository
    private var job: Job = Job()

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as TCEApplication).appComponent
            .injectBroadCastReceiver(this)
        //Toast.makeText(context, "Alarm received!--->", Toast.LENGTH_LONG).show()
        val result = loginRepository.extendToken()
        Log.d("SAN", "onResult-->$result")


    }

    private fun onResult(result: Boolean) {
        Log.d("SAN", "onResult-->$result")

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    companion object {

        @Synchronized
        fun setAlarm(context: Context, sessionTimeOut: Int) {
            val modifiedSessionTimeOut = sessionTimeOut - 30
            Log.d("SAN", "modifiedSessionTimeOut-->$modifiedSessionTimeOut")
            val milliSecond = modifiedSessionTimeOut * 1000

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().timeInMillis,
                (milliSecond).toLong(),
                alarmIntent
            )
        }

        fun cancelAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            val myIntent = Intent(
                getApplicationContext(),
                AlarmReceiver::class.java
            )
            val pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), 0, myIntent, 0
            )

            alarmManager!!.cancel(pendingIntent)
        }
    }
}