package com.ainsln.rhythmicabc.data

import android.content.Context
import com.ainsln.rhythmicabc.data.source.DefaultRhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicAlphabet
import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import com.ainsln.rhythmicabc.sound.impl.SoundPoolPlayer

interface AppContainer{
    val alphabet: RhythmicAlphabet
    val player: RhythmicPlayer
}

class DefaultAppContainer(private val context: Context): AppContainer {
    override val alphabet: RhythmicAlphabet by lazy {
        DefaultRhythmicAlphabet
    }

    override val player: RhythmicPlayer by lazy {
        SoundPoolPlayer(context)
    }

}
