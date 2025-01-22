package com.ainsln.rhythmicabc.ui.alphabet.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.sound.api.PlaybackState

enum class FabItem(@StringRes val titleRes: Int, val icon: ImageVector) {
    Play(titleRes = R.string.play, icon = Icons.Outlined.PlayCircle),
    Stop(titleRes = R.string.stop, icon = Icons.Outlined.StopCircle),
    Pause(titleRes = R.string.pause, icon = Icons.Outlined.PauseCircle),
    Resume(titleRes = R.string.resume, icon = Icons.Outlined.PlayCircle),
}

@Composable
fun MultiFAB(
    mainFab: FabItem,
    isExpanded: Boolean,
    items: List<FabItem>,
    onItemsClick: (FabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
    ) {

        if (isExpanded) {
            items.forEach { item ->
                Icon(
                    imageVector = item.icon,
                    contentDescription = stringResource(item.titleRes),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onItemsClick(item) }
                )
            }
        }

        Icon(
            imageVector = mainFab.icon,
            contentDescription = stringResource(mainFab.titleRes),
            modifier = Modifier
                .size(64.dp)
                .clickable { onItemsClick(mainFab) }
        )
    }
}


@Composable
fun PlaybackMultiFab(
    playbackState: PlaybackState,
    onFabClick: (FabItem) -> Unit
) {
    when (playbackState) {
        is PlaybackState.Idle -> {
            MultiFAB(
                mainFab = FabItem.Play,
                isExpanded = false,
                items = emptyList(),
                onItemsClick = onFabClick
            )
        }

        is PlaybackState.Playing -> {
            MultiFAB(
                mainFab = FabItem.Pause,
                isExpanded = true,
                items = listOf(FabItem.Stop),
                onItemsClick = onFabClick
            )
        }

        is PlaybackState.Paused -> {
            MultiFAB(
                mainFab = FabItem.Resume,
                isExpanded = true,
                items = listOf(FabItem.Stop),
                onItemsClick = onFabClick
            )
        }
    }
}
