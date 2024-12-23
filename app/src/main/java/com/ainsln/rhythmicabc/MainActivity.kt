package com.ainsln.rhythmicabc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ainsln.rhythmicabc.ui.alphabet.AlphabetScreen
import com.ainsln.rhythmicabc.ui.theme.RhythmicABCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RhythmicABCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlphabetScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

