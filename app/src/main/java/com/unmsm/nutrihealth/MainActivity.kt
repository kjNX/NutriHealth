package com.unmsm.nutrihealth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.AuthViewModel
import com.unmsm.nutrihealth.ui.composable.*
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

enum class MainScreen {
    Onboarding,
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
                val navController = rememberNavController()

                // 🔁 Estado para simular que se muestre solo una vez
                var showOnboarding by remember { mutableStateOf(true) }

                val goto = { path: String -> navController.navigate(path) }
                val navigate = { navController.navigate(MainScreen.Main.name) }
                val logout = { navController.navigate(MainScreen.Auth.name) }

                val login = { email: String, password: String ->
                    authViewModel.login(
                        email = email,
                        password = password,
                        onResult = { value: Boolean, msg: String ->
                            if (value) goto(MainScreen.Main.name)
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
                            if (value) goto(MainScreen.Main.name)
                            else Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = if (showOnboarding) MainScreen.Onboarding.name else MainScreen.Auth.name
                ) {
                    composable(MainScreen.Onboarding.name) {
                        OnboardingScreen(
                            onFinish = {
                                showOnboarding = false
                                goto(MainScreen.Auth.name)
                            }
                        )
                    }
                    composable(MainScreen.Auth.name) {
                        AuthDisplay(
                            onLogin = login,
                            onRegister = register,
                            onGoogleAccess = {},
                            onFacebookAccess = {}
                        )
                    }
                    composable(MainScreen.Main.name) {
                        MainDisplay(
                            onTopBarClick = listOf(
                                { goto(MainScreen.History.name) },
                                { goto(MainScreen.Profile.name) }
                            ),
                            onScanClick = { goto(MainScreen.Scan.name) },
                            onContactSelect = { contact ->
                                goto("${MainScreen.Messaging.name}/${contact.name}")
                            }
                        )
                    }
                    composable(MainScreen.Scan.name) {
                        Scan(onNavigate = navigate)
                    }
                    composable(MainScreen.History.name) {
                        History(onNavigate = navigate)
                    }
                    composable(MainScreen.Profile.name) {
                        Profile(onNavigate = navigate, onLogout = logout)
                    }
                    composable("${MainScreen.Messaging.name}/{contactName}") { backStackEntry ->
                        val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
                        val contact = getContacts().find { it.name == contactName } ?: Contact(contactName, "")
                        Messaging(contact = contact, onNavigate = navigate)
                    }
                }
            }
        }
    }
}
