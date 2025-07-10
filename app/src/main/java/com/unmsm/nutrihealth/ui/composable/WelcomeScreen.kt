package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.R

@Composable
fun WelcomeScreen(onStart: () -> Unit, onLogin: () -> Unit) {
    var rotation by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            rotation += 0.2f
            if (rotation >= 360f) rotation = 0f
            kotlinx.coroutines.delay(16) // ≈ 60 fps
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f)) // empuja la imagen a la derecha

                Image(
                    painter = painterResource(id = R.drawable.iamgenfondo),
                    contentDescription = "Decoración animada",
                    modifier = Modifier
                        .size(500.dp)
                        .offset(x = 90.dp) // 🔸 la empuja más a la derecha (puedes ajustar el valor)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.Center, // 🔹 centra el contenido en el eje horizontal
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_nutrihealth),
                    contentDescription = "Logo NutriHealth",
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido a NutriHealth",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu bienestar comienza aquí",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Comenzar")
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onLogin) {
                Text("¿Ya tienes una cuenta? Inicia sesión")
            }
        }
    }
}
