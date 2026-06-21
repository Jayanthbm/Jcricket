package com.jayanth.jcricket.wear

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.ambient.AmbientModeSupport
import com.jayanth.jcricket.wear.ui.theme.WearJcricketTheme
import com.jayanth.jcricket.wear.ui.viewmodel.WearMatchViewModel
import com.jayanth.jcricket.wear.ui.views.WearHomeScreen

class WearMainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private lateinit var matchViewModel: WearMatchViewModel
    private lateinit var ambientController: AmbientModeSupport.AmbientController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ambientController = AmbientModeSupport.attach(this)

        setContent {
            WearJcricketTheme {
                matchViewModel = viewModel()
                WearHomeScreen(viewModel = matchViewModel)
            }
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object : AmbientModeSupport.AmbientCallback() {
            override fun onEnterAmbient(ambientDetails: Bundle?) {
                super.onEnterAmbient(ambientDetails)
                if (::matchViewModel.isInitialized) {
                    matchViewModel.setAmbientMode(true)
                }
            }

            override fun onExitAmbient() {
                super.onExitAmbient()
                if (::matchViewModel.isInitialized) {
                    matchViewModel.setAmbientMode(false)
                }
            }

            override fun onUpdateAmbient() {
                super.onUpdateAmbient()
            }
        }
    }
}
