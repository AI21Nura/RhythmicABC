package com.ainsln.rhythmicabc.sound.impl

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.api.PlaybackState
import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import com.ainsln.rhythmicabc.sound.model.CurrentPlayback
import com.ainsln.rhythmicabc.sound.model.PlayerSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import javax.inject.Inject
import kotlin.math.min

class SoundPoolPlayer @Inject constructor(
    @ApplicationContext context: Context
) : RhythmicPlayer {

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()

    private var currentJob: Job? = null
    private val pauseMutex = Mutex()

    private val snareId: Int = soundPool.load(context, R.raw.snare, 1)
    private val ghostSnareId: Int = soundPool.load(context, R.raw.snare_ghost, 1)

    private var baseTimeNanos = 0L
    private var tickCount = 0

    private val _settings = MutableStateFlow(PlayerSettings())
    override val settings = _settings.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val state = _playbackState.asStateFlow()

    private val _currentPlayback = MutableStateFlow(CurrentPlayback())
    override val currentPlayback = _currentPlayback.asStateFlow()


    override fun playLetter(letter: RhythmicLetter) = play {
        playPatternLoop(letter) { index ->
            _currentPlayback.update {
                CurrentPlayback(letter = letter, elementIndex = index)
            }
        }
    }

    override fun playAlphabet(alphabet: List<RhythmicLetter>) = play {
        alphabet.forEach { letter ->
            playPatternLoop(letter) { index ->
                _currentPlayback.update {
                    CurrentPlayback(letter = letter, elementIndex = index)
                }
            }
        }
    }

    private fun play(block: suspend () -> Unit){
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            _playbackState.update { PlaybackState.Playing }
            block()
            _playbackState.update { PlaybackState.Idle }
            _currentPlayback.update { it.copy(letter = null) }
        }
    }

    private suspend fun playPatternLoop(letter: RhythmicLetter, sendData: suspend (Int) -> Unit) {
        baseTimeNanos = System.nanoTime()
        tickCount = 0
        var repeat = 0

        while (repeat++ < settings.value.letterRepeatCount) {
            letter.pattern.forEachIndexed { index, sound ->
                val startTime = System.nanoTime()
                sendData(index)
                if (sound)
                    soundPool.play(snareId, 1f, 1f, 1, 0, 1f)
                else if (settings.value.enableGhostNotes)
                    soundPool.play(ghostSnareId, 0.15f, 0.15f, 1, 0, 1f)

                preciseDelay(startTime)
            }
        }
    }

    override fun stop() {
        currentJob?.cancel()
        currentJob = null
        resetPauseState()
        _playbackState.update { PlaybackState.Idle }
        _currentPlayback.update { it.copy(letter = null) }
    }


    override fun pause() {
        pauseMutex.tryLock()
        _playbackState.update { PlaybackState.Paused }
    }


    override fun resume() {
        resetPauseState()
        _playbackState.update { PlaybackState.Playing }
    }

    override fun setBpm(bpm: Int) {
        _settings.update { it.copy(bpm = bpm) }
    }

    override fun toggleGhostNotes(enable: Boolean) {
        _settings.update { it.copy(enableGhostNotes = enable) }
    }

    override fun setLetterRepeatCount(count: Int) {
        _settings.update { it.copy(letterRepeatCount = count) }

    }

    override fun release() {
        stop()
        soundPool.release()
    }

    private suspend fun preciseDelay(startTimeNanos: Long) {
        val intervalNanos = calculateDelay(settings.value.bpm) * 1_000_000L

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
        if (pauseMutex.isLocked) {
            pauseMutex.unlock()
        }
    }
}
