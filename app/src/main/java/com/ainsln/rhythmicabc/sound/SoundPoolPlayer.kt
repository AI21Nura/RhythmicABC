package com.ainsln.rhythmicabc.sound

import android.content.Context
import android.media.SoundPool
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job

class SoundPoolPlayer(context: Context, initBpm: Int) : RhythmicPlayer {

    private val soundPool = SoundPool.Builder().build()
    private var currentJob: Job? = null

    override var bpm: Int = initBpm
    private val snareId: Int = soundPool.load(context, R.raw.snare, 1)

    override fun playLetter(letter: RhythmicLetter): Flow<Int> = channelFlow {
        currentJob = this.coroutineContext.job
        letter.pattern.forEachIndexed { index, sound ->
            send(index)
            if (sound) {
                soundPool.play(snareId, 1f, 1f, 1, 0, 1f)
            }
            delay(calculateDelay(bpm))
        }
    }

    override suspend fun setBpm(bpm: Int) {
        this.bpm = bpm
    }

    //channel will be closed when job is cancelled
    override fun stop() {
        currentJob?.cancel()
        currentJob = null
    }

    override fun release() {
        stop()
        soundPool.release()
    }

    private fun calculateDelay(bpm: Int): Long {
        val beatDuration = 60_000.0 / bpm
        return beatDuration.toLong()
    }

}
