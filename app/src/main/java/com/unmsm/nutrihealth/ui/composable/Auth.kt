package com.unmsm.nutrihealth.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text(text = title, style = MaterialTheme.typography.labelLarge)
        TextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
            placeholder = { Text(text = placeholder) },
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline
            )
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
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(painter = painterResource(id = icon), contentDescription = null)
        Text(
            text = "Continuar con $networkName",
            modifier = Modifier.padding(start = 8.dp)
        )
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
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo superior
        Image(
            painter = painterResource(id = R.drawable.logo_nutrihealth),
            contentDescription = "Logo NutriHealth",
            modifier = Modifier
                .width(96.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Bienvenido a NutriHealth",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = if (isLoggingIn) "Inicia sesión" else "Crea tu cuenta",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            if (!isLoggingIn) {
                EnhancedTextField(
                    value = name,
                    title = "Nombre",
                    icon = Icons.Default.People,
                    placeholder = "Ingrese nombre",
                    onValueChange = { name = it }
                )
            }
            EnhancedTextField(
                value = email,
                title = "Correo electrónico",
                icon = Icons.Default.Email,
                placeholder = "tucorreo@email.com",
                onValueChange = { email = it }
            )
            EnhancedTextField(
                value = password,
                title = "Contraseña",
                icon = Icons.Default.Password,
                placeholder = "Ingrese contraseña",
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.width(300.dp)) {
            Button(
                onClick = if (isLoggingIn) login else register,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = if (isLoggingIn) "Iniciar sesión" else "Registrarse")
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

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = if (isLoggingIn) "¿No tienes una cuenta? " else "¿Ya tienes una cuenta? ")
            Text(
                text = if (isLoggingIn) "Regístrate" else "Inicia sesión",
                modifier = Modifier.clickable { isLoggingIn = !isLoggingIn },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}
