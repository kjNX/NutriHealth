package com.unmsm.nutrihealth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.AuthViewModel
import com.unmsm.nutrihealth.logic.extension.hasAllPermission
import com.unmsm.nutrihealth.logic.extension.hasLocationPermission
import com.unmsm.nutrihealth.logic.extension.openAppSetting
import com.unmsm.nutrihealth.logic.utils.PermissionUtils
import com.unmsm.nutrihealth.ui.composable.*
import com.unmsm.nutrihealth.ui.composable.pages.map.HistoryScreen
import com.unmsm.nutrihealth.ui.compose.component.LocationPermissionRequestDialog
import com.unmsm.nutrihealth.ui.composable.settings.SettingsExport
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgument
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.ui.composable.dashboard.DashboardScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

enum class MainScreen {
    Onboarding,
    Auth,
    Setup,
    Main,
    Scan,
    History,
    Profile,
    Messaging,
    Welcome,
    Dashboard,
    Settings,
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var foodPredictionService: FoodPredictionService

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gotoAfterLogin: (String) -> Unit

    // Usar viewModels() para inyectar AuthViewModel
    private val authViewModel: AuthViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()
        val currentUser = Firebase.auth.currentUser
        //RR
        var startDestination = MainScreen.Welcome.name

        if (currentUser != null) {
            lifecycleScope.launch {
                try {
                    val doc = Firebase.firestore.collection("user").document(currentUser.uid).get().await()
                    val stage = (doc.get("stage") as? Long)?.toInt() ?: 0
                    User.id = currentUser.uid
                    User.stage = stage

                    // ⚠️ Establece startDestination basado en stage
                    startDestination = if (stage == 0) MainScreen.Setup.name else MainScreen.Main.name

                    // ⚠️ Ahora sí llama a setContent (dentro del launch)
                    launchUI(startDestination)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error obteniendo usuario", e)
                    launchUI(MainScreen.Welcome.name) // fallback si falla
                }
            }
        } else {
            launchUI(MainScreen.Welcome.name)
        }}

        private fun launchUI(startDestination: String) {
            setContent {
                val coroutineScope = rememberCoroutineScope()
                PermissionRequester() // Permisos ubicación


                NutriHealthTheme {
                    val navController = rememberNavController()
                    var showOnboarding by remember { mutableStateOf(true) }

                    val goto = { path: String -> navController.navigate(path) }
                    gotoAfterLogin = goto
                    val navigate = { navController.navigate(MainScreen.Main.name) }
                    val logout = { navController.navigate(MainScreen.Auth.name) }
                    // Login con correo y contraseña

                    val login = { email: String, password: String ->
                        coroutineScope.launch {
                            val result = authViewModel.login(email, password)
                            result.fold(
                                onSuccess = {
                                    Log.d("LOGIN_FLOW", "stage = ${User.stage}, navegando a pantalla")
                                    if (User.stage == 0) {
                                        goto(MainScreen.Setup.name)
                                    } else {
                                        goto(MainScreen.Main.name)
                                    }
                                },
                                onFailure = {
                                    Toast.makeText(baseContext, authViewModel.errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    val register = { name: String, email: String, password: String ->
                        coroutineScope.launch {
                            val result = authViewModel.signup(name, email, password)
                            result.fold(
                                onSuccess = {
                                    if (User.stage == 0) {
                                        goto(MainScreen.Setup.name)
                                    } else {
                                        goto(MainScreen.Main.name)
                                    }
                                },
                                onFailure = {
                                    Toast.makeText(baseContext, authViewModel.errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }



                    // Google SignIn config
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client))
                        .requestEmail()
                        .build()
                    googleSignInClient = GoogleSignIn.getClient(this, gso)

                    val onGoogleAccess = {
                        val intent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(intent)
                    }

                    val startDestination = if(FirebaseAuth.getInstance().currentUser != null) MainScreen.Main.name else MainScreen.Welcome.name

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ){
                        composable(MainScreen.Welcome.name) {
                            WelcomeScreen(
                                onStart = { navController.navigate("${MainScreen.Auth.name}?mode=register") },
                                onLogin = { navController.navigate("${MainScreen.Auth.name}?mode=login") }
                            )
                        }
                        composable(MainScreen.Onboarding.name) {
                            OnboardingFlow(
                                viewModel = hiltViewModel(),
                                onFinish = { navController.navigate(MainScreen.Auth.name) }
                            )
                        }
                        composable(
                            route = "${MainScreen.Auth.name}?mode={mode}",
                            arguments = listOf(
                                navArgument("mode") { defaultValue = "login" }
                            )
                        ) { backStackEntry ->
                            val mode = backStackEntry.arguments?.getString("mode") ?: "login"
                            val isLogin = mode == "login"

                            AuthDisplay(
                                onLogin = login,
                                onRegister = register,
                                onGoogleAccess = onGoogleAccess,
                                isLoginStart = isLogin
                            )
                        }
                        composable(MainScreen.Setup.name) {
                            AccountSetupDisplay(
                                navController = navController,
                                onSetupFinish = {
                                    val firestore = Firebase.firestore
                                    firestore.collection("user").document(User.id)
                                        .update("stage", 1)
                                        .addOnSuccessListener {
                                            User.stage = 1
                                            navController.navigate(MainScreen.Main.name) {
                                                popUpTo(MainScreen.Setup.name) { inclusive = true }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@MainActivity, "Error actualizando stage", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            )
                        }

                        composable(MainScreen.Main.name) {
                            MainDisplay(
                                navController = navController,
                                onTopBarClick = listOf(
                                    { goto(MainScreen.History.name) },
                                    { goto(MainScreen.Dashboard.name) },
                                    { goto(MainScreen.Profile.name) },
                                    { goto(MainScreen.Settings.name) }
                                ),
                                onScanClick = { goto(MainScreen.Scan.name) },
                                onContactSelect = { contact ->
                                    goto("${MainScreen.Messaging.name}/${contact.name}")
                                }
                            )
                        }

                        composable(MainScreen.Scan.name) {
                            Scan(
                                onNavigate = navigate,
                                foodPredictionService = foodPredictionService
                            )
                        }
                        composable(MainScreen.History.name) {
                            HistoryScreen(navController = navController)
                        }
                        composable(MainScreen.Profile.name) {
                            Profile(
                                onNavigate = navigate,
                                onLogout = logout
                            )
                        }
                        composable("${MainScreen.Messaging.name}/{contactName}") { backStack ->
                            val name = backStack.arguments?.getString("contactName") ?: ""
                            val contact = getContacts().find { it.name == name } ?: Contact(name, "")
                            Messaging(contact = contact, onNavigate = navigate)
                        }
                        composable(MainScreen.Settings.name) {
                            SettingsExport(
                                onBack = { navController.navigateUp() },
                                onLogout = {
                                    navController.navigate(MainScreen.Auth.name) {
                                        popUpTo(MainScreen.Main.name) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(MainScreen.Dashboard.name) {
                            DashboardScreen()
                        }
                    }
                }
            }
        }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    // Launch in a coroutine scope
                    lifecycleScope.launch {
                        val signInResult = authViewModel.loginWithGoogle(idToken)
                        signInResult.fold(
                            onSuccess = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Bienvenido, ${account.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // ✅ Redirige según el stage
                                val nextScreen = if (User.stage == 0) MainScreen.Setup.name else MainScreen.Main.name
                                gotoAfterLogin(nextScreen)
                            },
                            onFailure = {
                                Toast.makeText(this@MainActivity, authViewModel.errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                } ?: run {
                    Toast.makeText(this@MainActivity, "Token de Google no válido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error: ${e.statusCode}")
                Toast.makeText(this@MainActivity, "Error de inicio con Google", Toast.LENGTH_SHORT).show()
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
                    if (!activity.hasLocationPermission()) activity.finish()
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
