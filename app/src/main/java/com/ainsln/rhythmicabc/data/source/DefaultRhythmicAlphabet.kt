package com.ainsln.rhythmicabc.data.source

object DefaultRhythmicAlphabet : RhythmicAlphabet {

    private const val BINARY_PATTERNS_NUMBER = 16

    private val letters: List<RhythmicLetter> = listOf(
        //binary
        RhythmicLetter(name = "A", pattern = listOf(true, false, false, false)),
        RhythmicLetter(name = "B", pattern = listOf(false, true, false, false)),
        RhythmicLetter(name = "C", pattern = listOf(false, false, true, false)),
        RhythmicLetter(name = "D", pattern = listOf(false, false, false, true)),
        RhythmicLetter(name = "E", pattern = listOf(true, true, false, false)),
        RhythmicLetter(name = "F", pattern = listOf(false, true, true, false)),
        RhythmicLetter(name = "G", pattern = listOf(false, false, true, true)),
        RhythmicLetter(name = "H", pattern = listOf(true, false, false, true)),
        RhythmicLetter(name = "I", pattern = listOf(true, false, true, false)),
        RhythmicLetter(name = "J", pattern = listOf(false, true, false, true)),
        RhythmicLetter(name = "K", pattern = listOf(true, true, true, false)),
        RhythmicLetter(name = "L", pattern = listOf(false, true, true, true)),
        RhythmicLetter(name = "M", pattern = listOf(true, false, true, true)),
        RhythmicLetter(name = "N", pattern = listOf(true, true, false, true)),
        RhythmicLetter(name = "O", pattern = listOf(true, true, true, true)),
        RhythmicLetter(name = "P", pattern = listOf(false, false, false, false)),
        //ternary
        RhythmicLetter(name = "Q", pattern = listOf(true, false, false)),
        RhythmicLetter(name = "R", pattern = listOf(false, true, false)),
        RhythmicLetter(name = "S", pattern = listOf(false, false, true)),
        RhythmicLetter(name = "T", pattern = listOf(true, true, false)),
        RhythmicLetter(name = "U", pattern = listOf(false, true, true)),
        RhythmicLetter(name = "V", pattern = listOf(true, false, true)),
        RhythmicLetter(name = "W", pattern = listOf(true, true, true)),
        RhythmicLetter(name = "X", pattern = listOf(false, false, false))
    )

    override fun getBinaryLetters() = letters.subList(0, BINARY_PATTERNS_NUMBER)

    override fun getTernaryLetters() = letters.subList(BINARY_PATTERNS_NUMBER, letters.size)

    override fun getLetter(name: String): RhythmicLetter {
        return letters.first { it.name == name }
    }

}
