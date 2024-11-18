package com.ainsln.rhythmicabc.sound

import android.content.Context
import android.media.AudioAttributes
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
import kotlinx.coroutines.yield
import kotlin.math.min

class SoundPoolPlayer(context: Context, initBpm: Int) : RhythmicPlayer {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
    private var currentJob: Job? = null

    private val pauseMutex = Mutex()
    private var isPaused = false

    override var bpm: Int = initBpm
        private set
    override var enableGhostNotes: Boolean = true
        private set
    override var letterRepeatCount: Int = 1
        private set

    private val snareId: Int = soundPool.load(context, R.raw.snare, 1)
    private val ghostSnareId: Int = soundPool.load(context, R.raw.snare_ghost, 1)


    private var baseTimeNanos = 0L
    private var tickCount = 0

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
        baseTimeNanos = System.nanoTime()
        tickCount = 0
        var repeat = 0

        while (repeat++ < letterRepeatCount) {
            letter.pattern.forEachIndexed { index, sound ->
                val startTime = System.nanoTime()
                sendData(index)
                if (sound)
                    soundPool.play(snareId, 1f, 1f, 1, 0, 1f)
                else if (enableGhostNotes)
                    soundPool.play(ghostSnareId, 0.15f, 0.15f, 1, 0, 1f)

                preciseDelay(startTime)
            }
        }
    }


    //channel will be closed when job is cancelled
    override fun stop() {
        currentJob?.cancel()
        currentJob = null
        resetPauseState()
    }


    override fun pause() {
        isPaused = true
        pauseMutex.tryLock()
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

    override fun setLetterRepeatCount(count: Int) {
        letterRepeatCount = count
    }

    override fun release() {
        stop()
        soundPool.release()
    }

    private suspend fun preciseDelay(startTimeNanos: Long) {
        val intervalNanos = calculateDelay(bpm) * 1_000_000L

        tickCount++
        val elapsedTime = System.nanoTime() - startTimeNanos
        val targetTimeNanos = baseTimeNanos + (tickCount * intervalNanos) - elapsedTime

        var remainingNanos = targetTimeNanos - System.nanoTime()
        while (remainingNanos > 0) {
            pauseMutex.withLock { }
            remainingNanos = targetTimeNanos - System.nanoTime()
            val remainingMillis = remainingNanos / 1_000_000L
            if (remainingMillis > 0)
                delay(min(remainingMillis, 10))
            else
                yield()
        }

        if (-remainingNanos > intervalNanos / 2) {
            baseTimeNanos = System.nanoTime()
            tickCount = 0
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
