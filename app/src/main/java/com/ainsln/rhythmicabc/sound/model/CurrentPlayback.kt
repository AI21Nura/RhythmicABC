package com.ainsln.rhythmicabc.sound.model

import com.ainsln.rhythmicabc.data.source.RhythmicLetter

data class CurrentPlayback(
    val letter: RhythmicLetter? = null,
    val elementIndex: Int = 0
)
