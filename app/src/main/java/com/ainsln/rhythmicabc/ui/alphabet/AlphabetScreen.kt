package com.ainsln.rhythmicabc.ui.alphabet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ainsln.rhythmicabc.data.source.DefaultRhythmicAlphabet
import com.ainsln.rhythmicabc.data.source.RhythmicLetter
import com.ainsln.rhythmicabc.ui.alphabet.components.RhythmicLetterBox
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme

@Composable
fun AlphabetScreen(
    modifier: Modifier = Modifier,
    viewModel: AlphabetViewModel = viewModel(factory = AlphabetViewModel.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AlphabetScreenContent(
        uiState = uiState,
        onLetterClick = viewModel::playLetter,
        onStopClick = viewModel::stopPlayer,
        onBpmChange = viewModel::setBpm,
        onTabChange = viewModel::changeAlphabetTab,
        modifier = modifier
    )
}

@Composable
fun AlphabetScreenContent(
    uiState: AlphabetUiState,
    onLetterClick: (RhythmicLetter) -> Unit,
    onStopClick: () -> Unit,
    onBpmChange: (Int) -> Unit,
    onTabChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { AlphabetTabs.entries.size }

    LaunchedEffect(uiState.currentAlphabetTabIndex) {
        pagerState.scrollToPage(uiState.currentAlphabetTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        onTabChange(pagerState.currentPage)
    }

    Column(modifier.fillMaxSize()) {


        BpmSlider(
            value = uiState.bpm,
            onValueChanged = onBpmChange
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
                modifier = Modifier.padding(vertical = 8.dp).weight(1f)
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

@Composable
fun BpmSlider(
    value: Int,
    onValueChanged: (Int) -> Unit,
    minValue: Float = 40f,
    maxValue: Float = 300f
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = "BPM = $value")
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChanged(it.toInt()) },
            valueRange = minValue..maxValue
        )
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
            onLetterClick =  {},
            onBpmChange = {},
            onTabChange = {},
            onStopClick = {}
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
