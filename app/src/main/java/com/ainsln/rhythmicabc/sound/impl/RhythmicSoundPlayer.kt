package com.ainsln.rhythmicabc.sound.impl

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.api.PlaybackState
import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import com.ainsln.rhythmicabc.sound.impl.engine.SoundEngine
import com.ainsln.rhythmicabc.sound.model.CurrentPlayback
import com.ainsln.rhythmicabc.sound.model.PlayerSettings
import com.ainsln.rhythmicabc.sound.utils.TimeProvider
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

/**
 * This class handles the playback of rhythmic patterns using audio samples, simulating a metronome.
 * It plays a sequence of sounds at the specified beats per minute (BPM) with a given pattern.
 *
 * Important: The accuracy of the timing is not perfect due to system limitations.
 * This class provides a basic rhythm playback feature, but it is not suitable for precision-based metronome applications.
 */
class RhythmicSoundPlayer @Inject constructor(
    private val soundEngine: SoundEngine,
    private val timeProvider: TimeProvider
) : RhythmicPlayer {

    private var currentJob: Job? = null
    private val pauseMutex = Mutex()

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
        currentJob = CoroutineScope(Dispatchers.Default).launch {
            _playbackState.update { PlaybackState.Playing }
            block()
        }
        currentJob?.invokeOnCompletion {
            resetPauseState()
            _playbackState.update { PlaybackState.Idle }
            _currentPlayback.update { it.copy(letter = null) }
        }
    }

    private suspend fun playPatternLoop(letter: RhythmicLetter, sendData: suspend (Int) -> Unit) {
        baseTimeNanos = timeProvider.getCurrentTimeNanos()
        tickCount = 0
        var repeat = 0

        while (repeat++ < settings.value.letterRepeatCount) {
            letter.pattern.forEachIndexed { index, sound ->
                sendData(index)
                if (sound)
                    soundEngine.playSound()
                else if (settings.value.enableGhostNotes)
                    soundEngine.playGhost()
                preciseDelay()
            }
        }
    }

    override fun stop() {
        currentJob?.cancel()
        currentJob = null
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
        soundEngine.release()
    }

    private suspend fun preciseDelay() {
        val intervalNanos = calculateDelay(settings.value.bpm) * 1_000_000L

        tickCount++
        val targetTimeNanos = baseTimeNanos + (tickCount * intervalNanos)

        var remainingNanos = targetTimeNanos - timeProvider.getCurrentTimeNanos()
        while (remainingNanos > 0) {
            pauseMutex.withLock { }
            remainingNanos = targetTimeNanos - timeProvider.getCurrentTimeNanos()
            val remainingMillis = remainingNanos / 1_000_000L
            if (remainingMillis > 0)
                delay(min(remainingMillis, 10))
            else
                yield()
        }

        val overshoot = timeProvider.getCurrentTimeNanos() - targetTimeNanos
        if (overshoot > intervalNanos / 2) {
            baseTimeNanos = timeProvider.getCurrentTimeNanos()
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
