package com.ainsln.rhythmicabc.data.source

interface RhythmicAlphabet {
    fun getBinaryLetters(): List<RhythmicLetter>

    fun getTernaryLetters(): List<RhythmicLetter>

    fun getLetter(name: String): RhythmicLetter
}
