package com.ainsln.rhythmicabc.service

sealed interface NotificationPlaybackState {
    data class Playing(val letter: String?) : NotificationPlaybackState
    data class Paused(val letter: String?) : NotificationPlaybackState
    data object Idle : NotificationPlaybackState
}

fun NotificationPlaybackState.getLetter(): String{
    val letterOrNull = when(this){
        is NotificationPlaybackState.Playing -> letter
        is NotificationPlaybackState.Paused -> letter
        is NotificationPlaybackState.Idle -> null
    }
    return letterOrNull ?: "-"
}
