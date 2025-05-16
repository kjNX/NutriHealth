package com.unmsm.nutrihealth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.logic.AuthViewModel
import com.unmsm.nutrihealth.ui.composable.AuthDisplay
import com.unmsm.nutrihealth.ui.composable.History
import com.unmsm.nutrihealth.ui.composable.MainDisplay
import com.unmsm.nutrihealth.ui.composable.Messaging
import com.unmsm.nutrihealth.ui.composable.Profile
import com.unmsm.nutrihealth.ui.composable.Scan
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

enum class MainScreen() {
    Auth,
    Main,
    Scan,
    History,
    Profile,
    Messaging
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel = AuthViewModel()
        enableEdgeToEdge()
        setContent {
            NutriHealthTheme {
                var navController = rememberNavController()

                val goto = { path: String -> navController.navigate(path) }
                val navigate = { navController.navigate(MainScreen.Main.name) }
                val logout = { navController.navigate(MainScreen.Auth.name) }

                val login = { email: String, password: String ->
                    authViewModel.login(
                        email = email,
                        password = password,
                        onResult = { value: Boolean, msg: String ->
                            if(value) goto(MainScreen.Main.name)
                            else Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                val register = { name: String, email: String, password: String ->
                    authViewModel.signup(
                        name = name,
                        email = email,
                        password = password,
                        onResult = { value: Boolean, msg: String ->
                            if(value) goto(MainScreen.Main.name)
                            else Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = MainScreen.Auth.name,
                ) {
                    composable(route = MainScreen.Auth.name) {
                        AuthDisplay(
                            onLogin = login,
                            onRegister = register,
                            onGoogleAccess = {},
                            onFacebookAccess = {}
                        )
                    }
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
                        Profile(onNavigate = navigate, onLogout = logout)
                    }
                    composable(route = MainScreen.Messaging.name) {
                        Messaging(onNavigate = navigate)
                    }
                }
            }
        }
    }
}
