package com.unmsm.nutrihealth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.AuthViewModel
import com.unmsm.nutrihealth.logic.extension.hasAllPermission
import com.unmsm.nutrihealth.logic.extension.hasLocationPermission
import com.unmsm.nutrihealth.logic.extension.openAppSetting
import com.unmsm.nutrihealth.logic.utils.PermissionUtils
import com.unmsm.nutrihealth.ui.composable.*
import com.unmsm.nutrihealth.ui.composable.pages.map.HistoryScreen
import com.unmsm.nutrihealth.ui.compose.component.LocationPermissionRequestDialog
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels

enum class MainScreen {
    Onboarding, Auth, Main, Scan, History, Profile, Messaging
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gotoAfterLogin: (String) -> Unit

    // Usar viewModels() para inyectar AuthViewModel
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Facebook SDK Init
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        callbackManager = CallbackManager.Factory.create()

        setContent {
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
                    authViewModel.login(email, password) { success, msg ->
                        if (success) goto(MainScreen.Main.name)
                        else Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                // Registro con correo y contraseña

                val register = { name: String, email: String, password: String ->
                    authViewModel.signup(name, email, password) { success, msg ->
                        if (success) goto(MainScreen.Main.name)
                        else Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
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





                NavHost(
                    navController = navController,
                    startDestination = if (showOnboarding) MainScreen.Onboarding.name else MainScreen.Auth.name
                ) {
                    composable(MainScreen.Onboarding.name) {
                        OnboardingScreen {
                            showOnboarding = false
                            goto(MainScreen.Auth.name)
                        }
                    }
                    composable(MainScreen.Auth.name) {
                        AuthDisplay(
                            onLogin = login,
                            onRegister = register,
                            onGoogleAccess = onGoogleAccess,
                        )
                    }
                    composable(MainScreen.Main.name) {
                        MainDisplay(
                            navController = navController,
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

                    composable(MainScreen.Scan.name) { Scan(onNavigate = navigate) }
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
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    // Lanzador de Google
    val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    // Asegurarse de que idToken no es null antes de pasarlo
                    authViewModel.loginWithGoogle(idToken) { success, msg ->
                        if (success) {
                            Toast.makeText(
                                this,
                                "Bienvenido, ${account.displayName}",
                                Toast.LENGTH_SHORT
                            ).show()
                            gotoAfterLogin(MainScreen.Main.name)
                        } else {
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    // Manejar el caso en que idToken es null
                    Toast.makeText(this, "Token de Google no válido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error: ${e.statusCode}")
                Toast.makeText(this, "Error de inicio con Google", Toast.LENGTH_SHORT).show()
            }
        }


    // Función para iniciar sesión con Facebook
    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this@MainActivity) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                Toast.makeText(
                                    this@MainActivity,
                                    "Bienvenido, ${user?.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                gotoAfterLogin(MainScreen.Main.name)
                            } else {
                                val e = task.exception
                                if (e is FirebaseAuthUserCollisionException) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Cuenta ya existente. Intenta con otro método.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Log.e("FacebookLogin", "Error: ", e)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error autenticando con Facebook",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                }

                override fun onCancel() {
                    Toast.makeText(this@MainActivity, "Inicio cancelado", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error Facebook: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
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