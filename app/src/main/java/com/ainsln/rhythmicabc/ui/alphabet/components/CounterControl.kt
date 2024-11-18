package com.ainsln.rhythmicabc.ui.alphabet.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.IndeterminateCheckBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ainsln.rhythmicabc.R
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme

@Composable
fun CounterControl(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = { onValueChange(value - 1) }) {
            Icon(
                imageVector = Icons.Outlined.IndeterminateCheckBox,
                contentDescription = stringResource(R.string.subtract)
            )
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable { isDialogOpen = true }
        )

        IconButton(onClick = { onValueChange(value + 1) }) {
            Icon(
                imageVector = Icons.Outlined.AddBox,
                contentDescription = stringResource(R.string.add)
            )
        }
    }

    if (isDialogOpen) {
        InputDialog(
            value = value,
            onSaveClick = { input -> input.toIntOrNull()?.let { onValueChange(it) } },
            onCancelClick = { isDialogOpen = false }
        )
    }
}

@Composable
fun InputDialog(
    value: Int,
    onSaveClick: (String) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userText by remember { mutableStateOf(value.toString()) }
    Dialog(
        onDismissRequest = { onCancelClick() }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.reps_input),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = userText,
                    onValueChange = { userText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onCancelClick() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            onSaveClick(userText)
                            onCancelClick()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun InputDialogPreview() {
    RhythmicABCTheme {
        InputDialog(
            value = 1,
            onCancelClick = {},
            onSaveClick = {}
        )
    }
}
