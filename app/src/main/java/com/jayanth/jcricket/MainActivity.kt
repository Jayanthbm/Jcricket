package com.jayanth.jcricket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jayanth.jcricket.ui.theme.JcricketTheme
import com.jayanth.jcricket.ui.viewmodel.MatchViewModel
import com.jayanth.jcricket.ui.views.HomeScreen
import com.jayanth.jcricket.ui.views.MatchDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JcricketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val matchViewModel: MatchViewModel = viewModel()
                    val selectedMatch by matchViewModel.selectedMatch.collectAsState()

                    if (selectedMatch == null) {
                        HomeScreen(
                            viewModel = matchViewModel,
                            onMatchClick = { match -> matchViewModel.openMatch(match) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        MatchDetailsScreen(
                            viewModel = matchViewModel,
                            onBack = { matchViewModel.closeMatchDetails() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}