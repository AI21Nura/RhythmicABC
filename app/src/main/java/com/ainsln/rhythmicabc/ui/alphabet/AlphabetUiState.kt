package com.ainsln.rhythmicabc.ui.alphabet

import com.ainsln.rhythmicabc.data.source.RhythmicLetter

data class AlphabetUiState(
    val binaryLetters: List<RhythmicLetter> = emptyList(),
    val ternaryLetters: List<RhythmicLetter> = emptyList(),
    val currentAlphabetTabIndex: Int = AlphabetTabs.Binary.index,
    val currentLetter: RhythmicLetter? = null,
    val currentLetterElementIndex: Int = 0,
    val bpm: Int = AlphabetViewModel.INIT_BPM
)
