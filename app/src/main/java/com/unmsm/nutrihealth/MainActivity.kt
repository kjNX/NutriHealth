package com.unmsm.nutrihealth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.AuthViewModel
import com.unmsm.nutrihealth.logic.extension.hasAllPermission
import com.unmsm.nutrihealth.logic.extension.hasLocationPermission
import com.unmsm.nutrihealth.logic.extension.openAppSetting
import com.unmsm.nutrihealth.logic.utils.PermissionUtils
import com.unmsm.nutrihealth.ui.composable.*
import com.unmsm.nutrihealth.ui.compose.component.LocationPermissionRequestDialog
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import dagger.hilt.android.AndroidEntryPoint

enum class MainScreen {
    Onboarding, Auth, Main, Scan, History, Profile, Messaging
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PermissionRequester() // 👈 Solicita permisos de ubicación

            NutriHealthTheme {
                val navController = rememberNavController()
                var showOnboarding by remember { mutableStateOf(true) }

                val goto = { path: String -> navController.navigate(path) }
                val navigate = { navController.navigate(MainScreen.Main.name) }
                val logout = { navController.navigate(MainScreen.Auth.name) }

                val authViewModel = remember { AuthViewModel() }

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
                            navController = navController, // ✅ parámetro corregido
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
                        // Messaging(contact = contact, onNavigate = navigate)
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionRequester() {
        var showPermissionDeclinedRationale by rememberSaveable { mutableStateOf(false) }
        var showRationale by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val activity = context as Activity

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                it.forEach { (permission, isGranted) ->
                    if (!isGranted && PermissionUtils.locationPermissions.contains(permission)) {
                        showPermissionDeclinedRationale = true
                    }
                }
            }
        )

        if (showPermissionDeclinedRationale) {
            LocationPermissionRequestDialog(
                onDismissClick = {
                    if (!activity.hasLocationPermission())
                        activity.finish()
                    else showPermissionDeclinedRationale = false
                },
                onOkClick = { activity.openAppSetting() }
            )
        }

        if (showRationale) {
            LocationPermissionRequestDialog(
                onDismissClick = { activity.finish() },
                onOkClick = {
                    showRationale = false
                    permissionLauncher.launch(PermissionUtils.allPermissions)
                }
            )
        }

        LaunchedEffect(Unit) {
            when {
                activity.hasAllPermission() -> return@LaunchedEffect
                PermissionUtils.locationPermissions.any {
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
                } -> showRationale = true
                else -> permissionLauncher.launch(PermissionUtils.allPermissions)
            }
        }
    }
}
