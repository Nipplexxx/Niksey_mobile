package com.example.lotlinmessenger.оneSignalApplication

import android.app.Application
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "8d04aeae-8a31-4651-8d6d-8bc6e490e4e7"

class OneSignalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}