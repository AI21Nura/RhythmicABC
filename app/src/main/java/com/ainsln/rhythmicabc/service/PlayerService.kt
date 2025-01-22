package com.ainsln.rhythmicabc.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ainsln.rhythmicabc.MainActivity
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.RhythmicAbcApplication
import com.ainsln.rhythmicabc.sound.api.PlaybackState
import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : Service() {

    @Inject
    lateinit var player: RhythmicPlayer

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.name -> start()
            Actions.PAUSE.name -> pause()
            Actions.RESUME.name -> resume()
            Actions.STOP.name -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        CoroutineScope(Dispatchers.IO).launch {
            combine(player.state, player.currentPlayback){ state, playback ->
                when(state) {
                    is PlaybackState.Playing -> NotificationPlaybackState.Playing(playback.letter?.name)
                    is PlaybackState.Paused -> NotificationPlaybackState.Paused(playback.letter?.name)
                    is PlaybackState.Idle -> NotificationPlaybackState.Idle
                }
            }.collectLatest {
                notification(it)
            }
        }
    }

    private fun stop(){
        player.stop()
        stopSelf()
    }

    private fun pause(){
        player.pause()
    }

    private fun resume(){
        player.resume()
    }

    private fun notification(state: NotificationPlaybackState){
        val letter = state.getLetter()
        val isPlaying = state is NotificationPlaybackState.Playing

        val pendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            MainActivity.createIntent(baseContext),
            PendingIntent.FLAG_IMMUTABLE
        )

        val playerNotification = NotificationCompat
            .Builder(this, RhythmicAbcApplication.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_drum_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setSubText(getString(R.string.notification_subtitle))
            .setContentText(getString(R.string.notification_text, letter))
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(getString(R.string.notification_title))
                .bigText(getString(R.string.notification_text, letter))
            )
            .addAction(
                R.drawable.ic_stop,
                getString(R.string.stop),
                createPendingIntent(Actions.STOP)
            )
            .addAction(
                R.drawable.ic_play_pause,
                getString(if (isPlaying) R.string.pause else R.string.resume),
                createPendingIntent(if (isPlaying) Actions.PAUSE else Actions.RESUME)
            )
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(1, playerNotification)
    }

    private fun createPendingIntent(action: Actions): PendingIntent {
        return PendingIntent.getService(
            this, 0,
            Intent(this, PlayerService::class.java).setAction(action.name),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    enum class Actions {
        START, PAUSE, RESUME, STOP
    }
}
