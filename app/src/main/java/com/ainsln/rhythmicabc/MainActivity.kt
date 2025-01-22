package com.ainsln.rhythmicabc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.ainsln.rhythmicabc.service.PlayerService
import com.ainsln.rhythmicabc.ui.alphabet.AlphabetScreen
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requestNotificationPermission()

        setContent {
            RhythmicABCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlphabetScreen(
                        onStartService = {
                            controlService(PlayerService.Actions.START)
                        },
                        onStopService = {
                            controlService(PlayerService.Actions.STOP)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun controlService(action: PlayerService.Actions){
        Intent(this@MainActivity, PlayerService::class.java).also {
            it.action = action.name
            startService(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 0

        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }
}

