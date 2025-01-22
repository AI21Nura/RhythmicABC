package com.ainsln.rhythmicabc.sound.model

data class PlayerSettings(
    val bpm: Int = 60,
    val enableGhostNotes: Boolean = true,
    val letterRepeatCount: Int = 1
)
