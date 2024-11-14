package com.ainsln.rhythmicabc

import android.app.Application
import com.ainsln.rhythmicabc.data.AppContainer
import com.ainsln.rhythmicabc.data.DefaultAppContainer

class RhythmicAbcApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}
