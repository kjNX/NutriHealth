package com.unmsm.nutrihealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.unmsm.nutrihealth.ui.composable.MainDisplay
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriHealthTheme {
                MainDisplay()
            }
        }
    }
}
