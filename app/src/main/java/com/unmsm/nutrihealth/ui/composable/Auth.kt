package com.unmsm.nutrihealth.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R

@Composable
fun EnhancedTextField(
    value: String,
    title: String,
    icon: ImageVector,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        Text(text = title)
        TextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
            placeholder = { Text(text = placeholder) },
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SocialLoginButton(
    @DrawableRes icon: Int,
    networkName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick, modifier = modifier) {
        Icon(painter = painterResource(icon), contentDescription = null)
        Text(text = "Continuar con $networkName", modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun AuthDisplay(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onGoogleAccess: () -> Unit,
    onFacebookAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoggingIn by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val login = { onLogin(email, password) }
    val register = { onRegister(name, email, password) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a NutriHealth", style = MaterialTheme.typography.headlineLarge)
        Text(text = if(isLoggingIn) "Inicia sesión" else "Crea tu cuenta", style = MaterialTheme.typography.headlineSmall)
        Column {
            if (!isLoggingIn)
                EnhancedTextField(
                    value = name,
                    title = "Nombre",
                    icon = Icons.Default.People,
                    placeholder = "Ingrese nombre",
                    modifier = Modifier
                        .fillMaxWidth(),
                    onValueChange = { i -> name = i }
                )
            EnhancedTextField(
                value = email,
                title = "Correo electrónico",
                icon = Icons.Default.Email,
                placeholder = "tucorreo@email.com",
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { i -> email = i }
            )
            EnhancedTextField(
                value = password,
                title = "Contraseña",
                icon = Icons.Default.Password,
                placeholder = "Ingrese contraseña",
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { i -> password = i }
            )
        }
        Column(modifier = Modifier.width(300.dp)) {
            Button(
                onClick = if (isLoggingIn) login else register,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if(isLoggingIn) "Iniciar sesión" else "Registrarse")
            }
            SocialLoginButton(
                icon = R.drawable.google,
                networkName = "Google",
                onClick = onGoogleAccess,
                modifier = Modifier.fillMaxWidth()
            )
            SocialLoginButton(
                icon = R.drawable.facebook,
                networkName = "Facebook",
                onClick = onFacebookAccess,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row {
            Text(text = "¿${if(isLoggingIn) "No" else "Ya"} tienes una cuenta? ")
            Text(
                text = if (isLoggingIn) "Regístrate" else "Inicia sesión",
                modifier = Modifier.clickable(onClick = { isLoggingIn = !isLoggingIn })
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AuthDisplay({a, b -> }, {a, b, c ->}, {}, {})
}