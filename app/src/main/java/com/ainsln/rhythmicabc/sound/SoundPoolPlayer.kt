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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SoundPoolPlayer(context: Context, initBpm: Int) : RhythmicPlayer {

    private val soundPool = SoundPool.Builder().build()
    private var currentJob: Job? = null

    private val pauseMutex = Mutex()
    private var isPaused = false

    override var bpm: Int = initBpm
        private set
    override var enableGhostNotes: Boolean = true
        private set

    private val snareId: Int = soundPool.load(context, R.raw.snare, 1)
    private val ghostSnareId: Int = soundPool.load(context, R.raw.snare_ghost, 1)

    override fun playLetter(letter: RhythmicLetter): Flow<Int> = channelFlow {
        currentJob = this.coroutineContext.job
        playPatternLoop(letter) { index ->
            send(index)
        }
    }

    override fun playAlphabet(alphabet: List<RhythmicLetter>): Flow<Pair<RhythmicLetter, Int>> =
        channelFlow {
            currentJob = this.coroutineContext.job
            alphabet.forEach { letter ->
                playPatternLoop(letter) { index ->
                    send(Pair(letter, index))
                }
            }
        }

    private suspend fun playPatternLoop(letter: RhythmicLetter, sendData: suspend (Int) -> Unit) {
        letter.pattern.forEachIndexed { index, sound ->
            sendData(index)
            if (sound)
                soundPool.play(snareId, 1f, 1f, 1, 0, 1f)
            else if (enableGhostNotes)
                soundPool.play(ghostSnareId, 0.15f, 0.15f, 1, 0, 1f)

            pausableDelay(calculateDelay(bpm))
        }
    }

    //channel will be closed when job is cancelled
    override fun stop() {
        resetPauseState()
        currentJob?.cancel()
        currentJob = null
    }


    override suspend fun pause() {
        isPaused = true
        pauseMutex.lock()
    }

    override fun resume() {
        resetPauseState()
    }

    override fun setBpm(bpm: Int) {
        this.bpm = bpm
    }

    override fun toggleGhostNotes(enable: Boolean) {
        enableGhostNotes = enable
    }

    override fun release() {
        stop()
        soundPool.release()
    }

    private suspend fun pausableDelay(timeMillis: Long) {
        var remaining = timeMillis

        while (remaining > 0) {
            pauseMutex.withLock { }
            val beforeDelay = System.currentTimeMillis()
            delay(10)
            val afterDelay = System.currentTimeMillis()
            remaining -= (afterDelay - beforeDelay)
        }
    }

    private fun calculateDelay(bpm: Int): Long {
        val beatDuration = 60_000.0 / bpm
        return beatDuration.toLong()
    }

    private fun resetPauseState() {
        isPaused = false
        if (pauseMutex.isLocked) {
            pauseMutex.unlock()
        }
    }

}
