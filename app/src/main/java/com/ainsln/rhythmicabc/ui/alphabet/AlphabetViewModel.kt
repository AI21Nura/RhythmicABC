package com.ainsln.rhythmicabc.ui.alphabet

import android.util.Log
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlphabetViewModel(
    private val alphabet: RhythmicAlphabet,
    private val player: RhythmicPlayer
) : ViewModel() {

    val uiState: MutableStateFlow<AlphabetUiState> = MutableStateFlow(AlphabetUiState(
        binaryLetters = alphabet.getBinaryLetters(),
        ternaryLetters = alphabet.getTernaryLetters()
    ))

    fun playLetter(letter: RhythmicLetter){
        stopPlayer()
        viewModelScope.launch {
            player.playLetter(letter)
                .onStart { updateCurrentLatter(letter) }
                .onCompletion { updateCurrentLatter(null) }
                .collectLatest { elementIndex ->
                    uiState.update { oldState -> oldState.copy(currentLetterElementIndex = elementIndex)
                }
            }
        }
    }

    fun stopPlayer(){
        if (uiState.value.currentLetter != null)
            player.stop()
    }

    fun setBpm(newBpm: Int){
        uiState.update { oldState -> oldState.copy(bpm = newBpm) }
        viewModelScope.launch {
            player.setBpm(newBpm)
        }
    }

    fun changeAlphabetTab(tabIndex: Int){
        uiState.update { oldState -> oldState.copy(currentAlphabetTabIndex = tabIndex) }
        Log.d("TAG", "${uiState.value.currentAlphabetTabIndex}")
    }

    private fun updateCurrentLatter(letter: RhythmicLetter?){
        uiState.update { oldState -> oldState.copy(currentLetter = letter) }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
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
