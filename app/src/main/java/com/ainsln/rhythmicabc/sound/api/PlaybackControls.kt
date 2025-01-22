package com.ainsln.rhythmicabc.sound.api

interface PlaybackControls {
    fun pause()

    fun stop()

    fun resume()

    fun setBpm(bpm: Int)

    fun toggleGhostNotes(enable: Boolean)

    fun setLetterRepeatCount(count: Int)
}
