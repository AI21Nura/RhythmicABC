package com.ainsln.rhythmicabc.ui.alphabet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ainsln.rhythmicabc.RhythmicAbcApplication
import com.ainsln.rhythmicabc.data.source.RhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.RhythmicPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlphabetViewModel(
    private val alphabet: RhythmicAlphabet,
    private val player: RhythmicPlayer
) : ViewModel(), PlaybackControls {

    private val _uiState: MutableStateFlow<AlphabetUiState> = MutableStateFlow(
        AlphabetUiState(
            binaryLetters = alphabet.getBinaryLetters(),
            ternaryLetters = alphabet.getTernaryLetters()
        )
    )

    val uiState: StateFlow<AlphabetUiState> = _uiState

    fun playLetter(letter: RhythmicLetter) {
        stop()
        viewModelScope.launch {
            player.playLetter(letter)
                .onStart { updateCurrentLetter(letter) }
                .onCompletion { updateCurrentLetter(null) }
                .collectLatest { elementIndex ->
                    _uiState.update { oldState ->
                        oldState.copy(currentLetterElementIndex = elementIndex)
                    }
                }
        }
    }

    fun playAlphabet() {
        stop()
        viewModelScope.launch {
            player.playAlphabet(alphabet = alphabet.getAll())
                .onStart { updatePlaybackState(PlaybackState.Playing) }
                .onCompletion {
                    updateCurrentLetter(null)
                    updatePlaybackState(PlaybackState.Stopped)
                }
                .collectLatest { data ->
                    _uiState.update { oldState ->
                        oldState.copy(
                            currentLetter = data.first,
                            currentLetterElementIndex = data.second,
                            currentAlphabetTabIndex = if (data.first.name.first() >= 'Q')
                                AlphabetTabs.Ternary.index
                            else
                                AlphabetTabs.Binary.index
                        )
                    }
                }
        }
    }

    override fun stop() {
        if (_uiState.value.currentLetter != null){
            player.stop()
            updateCurrentLetter(null)
            updatePlaybackState(PlaybackState.Stopped)
        }
    }

    override fun pause(){
        updatePlaybackState(PlaybackState.Paused)
        player.pause()
    }

    override fun resume(){
        updatePlaybackState(PlaybackState.Playing)
        player.resume()
    }

    override fun setBpm(newBpm: Int) {
        _uiState.update { oldState -> oldState.copy(bpm = newBpm) }
        viewModelScope.launch {
            player.setBpm(newBpm)
        }
    }

    override fun toggleGhostNotes(enable: Boolean){
        player.toggleGhostNotes(enable)
        _uiState.update { oldState -> oldState.copy(enableGhostNotes = enable) }
    }

    override fun setLetterRepeatCount(count: Int){
        if (count in 1..100){
            _uiState.update { oldState -> oldState.copy(letterRepeatCount = count) }
            player.setLetterRepeatCount(count)
        }
    }

    fun changeAlphabetTab(tabIndex: Int) {
        _uiState.update { oldState -> oldState.copy(currentAlphabetTabIndex = tabIndex) }
    }

    private fun updateCurrentLetter(letter: RhythmicLetter?) {
        _uiState.update { oldState ->
            oldState.copy(currentLetter = letter)
        }
    }

    private fun updatePlaybackState(state: PlaybackState){
        _uiState.update { oldState -> oldState.copy(alphabetPlaybackState = state) }
    }

    companion object {
        const val INIT_BPM = 60

        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as RhythmicAbcApplication).container
                AlphabetViewModel(alphabet = container.alphabet, player = container.player)
            }
        }
    }
}
