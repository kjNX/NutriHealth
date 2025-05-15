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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R

@Composable
fun EnhancedTextField(
    title: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = title)
        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Row {
                    Icon(imageVector = icon, contentDescription = null)
                    Text(text = placeholder, modifier = Modifier.padding(start = 16.dp))
                } },
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
fun AuthDisplay(modifier: Modifier = Modifier) {
    var isLoggingIn by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a NutriHealth", style = MaterialTheme.typography.headlineLarge)
        Text(text = if(isLoggingIn) "Inicia sesión" else "Crea tu cuenta", style = MaterialTheme.typography.headlineSmall)
        Column {
            if (!isLoggingIn)
                EnhancedTextField(
                    title = "Nombre",
                    icon = Icons.Default.People,
                    placeholder = "Ingrese nombre",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            EnhancedTextField(
                title = "Correo electrónico",
                icon = Icons.Default.Email,
                placeholder = "tucorreo@email.com",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            EnhancedTextField(
                title = "Contraseña",
                icon = Icons.Default.Password,
                placeholder = "Ingrese contraseña",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        Column(modifier = Modifier.width(300.dp)) {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Iniciar sesión")
            }
            SocialLoginButton(
                icon = R.drawable.google,
                networkName = "Google",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
            SocialLoginButton(
                icon = R.drawable.facebook,
                networkName = "Facebook",
                onClick = {},
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
    AuthDisplay()
}