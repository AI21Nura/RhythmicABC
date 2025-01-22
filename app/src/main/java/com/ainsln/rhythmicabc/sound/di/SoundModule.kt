package com.ainsln.rhythmicabc.sound.di

import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import com.ainsln.rhythmicabc.sound.impl.SoundPoolPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SoundModule {

    @Singleton
    @Binds
    fun bindsRhythmicPlayer(
        player: SoundPoolPlayer
    ): RhythmicPlayer

}
