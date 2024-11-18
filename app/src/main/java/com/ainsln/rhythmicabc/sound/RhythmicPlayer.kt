package com.ainsln.rhythmicabc.sound

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.ui.alphabet.PlaybackControls
import kotlinx.coroutines.flow.Flow

interface RhythmicPlayer : PlaybackControls {

    val bpm: Int
    val enableGhostNotes: Boolean
    val letterRepeatCount: Int

    fun playLetter(letter: RhythmicLetter) : Flow<Int>

    fun playAlphabet(alphabet: List<RhythmicLetter>) : Flow<Pair<RhythmicLetter, Int>>

    fun release()

}
