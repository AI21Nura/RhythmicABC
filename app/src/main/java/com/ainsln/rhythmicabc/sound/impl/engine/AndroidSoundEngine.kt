package com.ainsln.rhythmicabc.sound.impl.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ainsln.rhythmicabc.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidSoundEngine @Inject constructor(
    @ApplicationContext context: Context
) : SoundEngine {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
    private val snareId: Int = soundPool.load(context, R.raw.snare, 1)
    private val ghostSnareId: Int = soundPool.load(context, R.raw.snare_ghost, 1)

    override fun playSound() {
        soundPool.play(snareId, 1f, 1f, 1, 0, 1f)
    }

    override fun playGhost() {
        soundPool.play(ghostSnareId, 0.15f, 0.15f, 1, 0, 1f)
    }

    override fun release() {
        soundPool.release()
    }
}
