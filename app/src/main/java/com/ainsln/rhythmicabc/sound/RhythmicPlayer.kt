package com.ainsln.rhythmicabc.sound

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import kotlinx.coroutines.flow.Flow

interface RhythmicPlayer {

    var bpm: Int

    fun playLetter(letter: RhythmicLetter) : Flow<Int>

    suspend fun setBpm(bpm: Int)

    fun stop()

    fun release()

}
