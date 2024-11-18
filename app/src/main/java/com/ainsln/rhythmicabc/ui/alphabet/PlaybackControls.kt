package com.ainsln.rhythmicabc.ui.alphabet

interface PlaybackControls {
    fun pause()

    fun stop()

    fun resume()

    fun setBpm(bpm: Int)

    fun toggleGhostNotes(enable: Boolean)

    fun setLetterRepeatCount(count: Int)
}
