package com.ainsln.rhythmicabc.ui.alphabet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.data.source.DefaultRhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.sound.api.PlaybackControls
import com.ainsln.rhythmicabc.ui.alphabet.components.CounterControl
import com.ainsln.rhythmicabc.ui.alphabet.components.FabItem
import com.ainsln.rhythmicabc.ui.alphabet.components.PlaybackMultiFab
import com.ainsln.rhythmicabc.ui.alphabet.components.RhythmicLetterBox
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme

@Composable
fun AlphabetScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AlphabetScreenContent(
        uiState = uiState,
        onLetterClick = viewModel::playLetter,
        onPlayClick = {
            viewModel.playAlphabet()
            onStartService()
        },
        onStopClick = {
            viewModel.stop()
            onStopService()
        },
        playbackControls = viewModel,
        onTabChange = viewModel::changeAlphabetTab,
        modifier = modifier
    )
}

@Composable
fun AlphabetScreenContent(
    uiState: AlphabetUiState,
    onLetterClick: (RhythmicLetter) -> Unit,
    onPlayClick: () -> Unit,
    onStopClick: () -> Unit,
    onTabChange: (Int) -> Unit,
    playbackControls: PlaybackControls,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { AlphabetTabs.entries.size }

    LaunchedEffect(uiState.currentAlphabetTabIndex) {
        pagerState.scrollToPage(uiState.currentAlphabetTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        onTabChange(pagerState.currentPage)
    }

    Scaffold(
        floatingActionButton = {
            PlaybackMultiFab(
                playbackState = uiState.alphabetPlaybackState,
                onFabClick = { fabItem ->
                    when(fabItem){
                        FabItem.Play -> onPlayClick()
                        FabItem.Stop -> onStopClick()
                        FabItem.Pause -> playbackControls.pause()
                        FabItem.Resume -> playbackControls.resume()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)) {

            BpmSlider(
                value = uiState.bpm,
                onValueChanged = playbackControls::setBpm
            )

            GhostSwitch(
                enableGhostNotes = uiState.enableGhostNotes,
                onValueChange = playbackControls::toggleGhostNotes
            )

            RepeatControl(
                value = uiState.letterRepeatCount,
                onValueChange = playbackControls::setLetterRepeatCount
            )

            TabRow(selectedTabIndex = uiState.currentAlphabetTabIndex) {
                AlphabetTabs.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.currentAlphabetTabIndex == index,
                        onClick = { onTabChange(index) },
                        text = { Text(tab.title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
            ) { pageIndex ->
                when (pageIndex) {
                    AlphabetTabs.Binary.index -> {
                        AlphabetGrid(
                            letters = uiState.binaryLetters,
                            onLetterClick = onLetterClick,
                            currentLetter = uiState.currentLetter,
                            currentElementIndex = uiState.currentLetterElementIndex
                        )
                    }

                    AlphabetTabs.Ternary.index -> {
                        AlphabetGrid(
                            letters = uiState.ternaryLetters,
                            onLetterClick = onLetterClick,
                            currentLetter = uiState.currentLetter,
                            currentElementIndex = uiState.currentLetterElementIndex
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun BpmSlider(
    value: Int,
    onValueChanged: (Int) -> Unit,
    minValue: Float = 40f,
    maxValue: Float = 250f
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = stringResource(R.string.bpm, value))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChanged(it.toInt()) },
            valueRange = minValue..maxValue
        )
    }
}

@Composable
fun GhostSwitch(
    enableGhostNotes: Boolean,
    onValueChange: (Boolean) -> Unit
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(text = stringResource(R.string.ghost_label))
        Spacer(Modifier.weight(1f))
        Switch(
            checked = enableGhostNotes,
            onCheckedChange = { onValueChange(it) }
        )
    }
}

@Composable
fun RepeatControl(
    value: Int,
    onValueChange: (Int) -> Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        Text(stringResource(R.string.repeat_count))
        Spacer(Modifier.weight(1f))
        CounterControl(value, onValueChange)
    }
}

@Composable
fun AlphabetGrid(
    letters: List<RhythmicLetter>,
    onLetterClick: (RhythmicLetter) -> Unit,
    currentLetter: RhythmicLetter?,
    currentElementIndex: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3)
        ) {
            items(items = letters) { letter ->
                RhythmicLetterBox(
                    letter = letter,
                    onLetterClick = onLetterClick,
                    isPlaying = currentLetter?.name == letter.name,
                    currentElementIndex = currentElementIndex
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlphabetScreenContentPreview() {
    RhythmicABCTheme {
        AlphabetScreenContent(
            uiState = AlphabetUiState(binaryLetters = DefaultRhythmicAlphabet.getBinaryLetters()),
            onLetterClick = {},
            onPlayClick = {},
            onStopClick = {},
            onTabChange = {},
            playbackControls = object : PlaybackControls {
                override fun pause() {}
                override fun stop() {}
                override fun resume() {}
                override fun setBpm(bpm: Int) {}
                override fun toggleGhostNotes(enable: Boolean) {}
                override fun setLetterRepeatCount(count: Int) {}
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BpmSliderPreview() {
    RhythmicABCTheme {
        BpmSlider(
            value = 60,
            onValueChanged = {}
        )
    }
}
