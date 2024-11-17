package com.ainsln.rhythmicabc.ui.alphabet

sealed interface PlaybackState {
    data object Playing : PlaybackState
    data object Paused : PlaybackState
    data object Stopped : PlaybackState
}
