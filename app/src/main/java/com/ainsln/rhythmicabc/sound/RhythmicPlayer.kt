package com.ainsln.rhythmicabc.sound

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import kotlinx.coroutines.flow.Flow

interface RhythmicPlayer {

    var bpm: Int

    fun playLetter(letter: RhythmicLetter) : Flow<Int>

    fun playAlphabet(alphabet: List<RhythmicLetter>) : Flow<Pair<RhythmicLetter, Int>>

    fun stop()

    suspend fun pause()

    fun resume()

    suspend fun setBpm(bpm: Int)

    fun release()

}
