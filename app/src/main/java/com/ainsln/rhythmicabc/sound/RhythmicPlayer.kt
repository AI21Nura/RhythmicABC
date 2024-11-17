package com.ainsln.rhythmicabc.sound

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import kotlinx.coroutines.flow.Flow

interface RhythmicPlayer {

    val bpm: Int
    val enableGhostNotes: Boolean

    fun playLetter(letter: RhythmicLetter) : Flow<Int>

    fun playAlphabet(alphabet: List<RhythmicLetter>) : Flow<Pair<RhythmicLetter, Int>>

    fun stop()

    suspend fun pause()

    fun resume()

    fun setBpm(bpm: Int)

    fun toggleGhostNotes(enable: Boolean)

    fun release()

}
