package com.unmsm.nutrihealth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gotoAfterLogin: (String) -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel = AuthViewModel()

        enableEdgeToEdge()
        setContent {
            NutriHealthTheme {
                val navController = rememberNavController()
                var showOnboarding by remember { mutableStateOf(true) }

                val goto = { path: String -> navController.navigate(path) }
                gotoAfterLogin = goto
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

                // Configurar Google Sign-In
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client))  // Usa el ID de cliente que agregaste en `strings.xml`
                    .requestEmail()
                    .build()

                googleSignInClient = GoogleSignIn.getClient(this, gso)

                // Iniciar sesión con Google
                val onGoogleAccess = {
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)  // Lanza el flujo de inicio de sesión
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
                            onGoogleAccess = onGoogleAccess,  // Pasa correctamente la función aquí
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

    // Lanzar el flujo de inicio de sesión con Google
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val idToken = account.idToken
                    signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignInError", "Error de inicio de sesión con Google: ${e.statusCode}")  // Muestra el código de error.
                Toast.makeText(this, "Error de inicio de sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }


    // Método para autenticar a Firebase con el idToken de Google
    private fun signInWithGoogle(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // El usuario ha iniciado sesión exitosamente
                        val user = FirebaseAuth.getInstance().currentUser
                        Toast.makeText(this, "Bienvenido, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                        gotoAfterLogin(MainScreen.Main.name) // <<<< Navegar a la pantalla principal
                    } else {
                        // Si el inicio de sesión falla
                        Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
