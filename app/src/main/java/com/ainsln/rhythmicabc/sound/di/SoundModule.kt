package com.ainsln.rhythmicabc.sound.di

import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import com.ainsln.rhythmicabc.sound.impl.engine.SoundEngine
import com.ainsln.rhythmicabc.sound.impl.engine.AndroidSoundEngine
import com.ainsln.rhythmicabc.sound.impl.RhythmicSoundPlayer
import com.ainsln.rhythmicabc.sound.utils.SystemTimeProvider
import com.ainsln.rhythmicabc.sound.utils.TimeProvider
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
        player: RhythmicSoundPlayer
    ): RhythmicPlayer

    @Singleton
    @Binds
    fun bindsSoundEngine(
        engine: AndroidSoundEngine
    ): SoundEngine

    @Singleton
    @Binds
    fun bindsTimeProvider(
        provider: SystemTimeProvider
    ): TimeProvider

}
