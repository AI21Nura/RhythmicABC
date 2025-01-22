package com.ainsln.rhythmicabc.ui.alphabet

import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.api.PlaybackState

data class AlphabetUiState(
    val binaryLetters: List<RhythmicLetter> = emptyList(),
    val ternaryLetters: List<RhythmicLetter> = emptyList(),
    val currentAlphabetTabIndex: Int = AlphabetTabs.Binary.index,

    val alphabetPlaybackState: PlaybackState = PlaybackState.Idle,
    val currentLetter: RhythmicLetter? = null,
    val currentLetterElementIndex: Int = 0,

    val bpm: Int = AlphabetViewModel.INIT_BPM,
    val enableGhostNotes: Boolean = true,
    val letterRepeatCount: Int = 1
)
