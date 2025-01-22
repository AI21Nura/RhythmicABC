package com.ainsln.rhythmicabc.sound.api

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.model.CurrentPlayback
import com.ainsln.rhythmicabc.sound.model.PlayerSettings
import kotlinx.coroutines.flow.StateFlow

interface RhythmicPlayer : PlaybackControls {
    val settings: StateFlow<PlayerSettings>
    val state: StateFlow<PlaybackState>
    val currentPlayback: StateFlow<CurrentPlayback>

    fun playLetter(letter: RhythmicLetter)
    fun playAlphabet(alphabet: List<RhythmicLetter>)
    fun release()
}
