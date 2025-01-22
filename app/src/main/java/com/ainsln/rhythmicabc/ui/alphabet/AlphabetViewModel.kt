package com.ainsln.rhythmicabc.ui.alphabet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ainsln.rhythmicabc.data.source.RhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.api.PlaybackControls
import com.ainsln.rhythmicabc.sound.api.RhythmicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AlphabetViewModel @Inject constructor(
    private val alphabet: RhythmicAlphabet,
    private val player: RhythmicPlayer
) : ViewModel(), PlaybackControls {

    private val binaryLetters = alphabet.getBinaryLetters()
    private val ternaryLetters = alphabet.getTernaryLetters()
    private val currentTabIndex = MutableStateFlow(AlphabetTabs.Binary.index)

    val uiState: StateFlow<AlphabetUiState> = combine(
        player.state,
        player.currentPlayback,
        player.settings,
        currentTabIndex
    ) { playbackState, currentPlayback, settings, currentTab ->
        currentPlayback.letter?.let { letter ->
            if (letter.name.first() == 'Q' && currentTab != AlphabetTabs.Ternary.index)
                changeAlphabetTab(AlphabetTabs.Ternary.index)
        }
        AlphabetUiState(
            binaryLetters = binaryLetters,
            ternaryLetters = ternaryLetters,
            alphabetPlaybackState = playbackState,
            currentLetter = currentPlayback.letter,
            currentLetterElementIndex = currentPlayback.elementIndex,
            bpm = settings.bpm,
            enableGhostNotes = settings.enableGhostNotes,
            letterRepeatCount = settings.letterRepeatCount,
            currentAlphabetTabIndex = currentTab
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AlphabetUiState()
    )

    fun playLetter(letter: RhythmicLetter) {
        stop()
        player.playLetter(letter)
    }

    fun playAlphabet() {
        stop()
        player.playAlphabet(alphabet.getAll())
    }

    override fun stop() {
        player.stop()
    }

    override fun pause(){
        player.pause()
    }

    override fun resume(){
        player.resume()
    }

    override fun setBpm(bpm: Int) {
        player.setBpm(bpm)
    }

    override fun toggleGhostNotes(enable: Boolean){
        player.toggleGhostNotes(enable)
    }

    override fun setLetterRepeatCount(count: Int){
        if (count in 1..100){
            player.setLetterRepeatCount(count)
        }
    }

    fun changeAlphabetTab(tabIndex: Int) {
        currentTabIndex.update { tabIndex }
    }

    companion object {
        const val INIT_BPM = 60
    }
}
