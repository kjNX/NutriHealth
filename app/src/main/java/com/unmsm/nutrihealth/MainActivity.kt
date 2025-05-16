package com.unmsm.nutrihealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.ui.composable.History
import com.unmsm.nutrihealth.ui.composable.MainDisplay
import com.unmsm.nutrihealth.ui.composable.Messaging
import com.unmsm.nutrihealth.ui.composable.Profile
import com.unmsm.nutrihealth.ui.composable.Scan
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

enum class MainScreen() {
    Main,
    Scan,
    History,
    Profile,
    Messaging
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriHealthTheme {
                var navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MainScreen.Main.name,
                ) {
                    val goto = { path: String -> navController.navigate(path) }
                    val navigate = { navController.navigate(MainScreen.Main.name) }

                    composable(route = MainScreen.Main.name) {
                        MainDisplay(
                            onTopBarClick = listOf(
                                { goto(MainScreen.History.name) },
                                { goto(MainScreen.Profile.name) }
                            ),
                            onScanClick = { goto(MainScreen.Scan.name) },
                            onContactSelect = { goto(MainScreen.Messaging.name) }
                        )
                    }
                    composable(route = MainScreen.Scan.name) {
                        Scan(onNavigate = navigate)
                    }
                    composable(route = MainScreen.History.name) {
                        History(onNavigate = navigate)
                    }
                    composable(route = MainScreen.Profile.name) {
                        Profile(onNavigate = navigate)
                    }
                    composable(route = MainScreen.Messaging.name) {
                        Messaging(onNavigate = navigate)
                    }
                }
            }
        }
    }
}
