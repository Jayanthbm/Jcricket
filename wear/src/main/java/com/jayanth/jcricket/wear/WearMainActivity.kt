package com.jayanth.jcricket.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.ambient.AmbientLifecycleObserver
import com.jayanth.jcricket.wear.ui.theme.WearJcricketTheme
import com.jayanth.jcricket.wear.ui.viewmodel.WearMatchViewModel
import com.jayanth.jcricket.wear.ui.views.WearHomeScreen

class WearMainActivity : ComponentActivity() {
    private lateinit var matchViewModel: WearMatchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
            override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
                if (::matchViewModel.isInitialized) {
                    matchViewModel.setAmbientMode(true)
                }
            }

            override fun onExitAmbient() {
                if (::matchViewModel.isInitialized) {
                    matchViewModel.setAmbientMode(false)
                }
            }

            override fun onUpdateAmbient() {
                // No-op or periodic update if needed
            }
        }

        val ambientLifecycleObserver = AmbientLifecycleObserver(this, callback)
        lifecycle.addObserver(ambientLifecycleObserver)

        setContent {
            WearJcricketTheme {
                matchViewModel = viewModel()
                WearHomeScreen(viewModel = matchViewModel)
            }
        }
    }
}
