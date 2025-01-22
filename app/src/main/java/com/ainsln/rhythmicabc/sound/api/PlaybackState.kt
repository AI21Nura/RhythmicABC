package com.ainsln.rhythmicabc.sound.api

sealed interface PlaybackState {
    data object Playing : PlaybackState
    data object Paused : PlaybackState
    data object Idle : PlaybackState
}
