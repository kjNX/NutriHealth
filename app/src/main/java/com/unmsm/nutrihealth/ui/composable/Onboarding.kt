package com.unmsm.nutrihealth.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.data.model.CurrentUser
import com.unmsm.nutrihealth.data.model.UserInitial
import com.unmsm.nutrihealth.data.model.UserPlan
import com.unmsm.nutrihealth.data.model.UserTarget

import com.unmsm.nutrihealth.logic.AuthViewModel


// ✅ REFACTORED Onboarding Compose Flow – recolectar información paso a paso sin pantalla de bienvenida

@Composable
fun OnboardingFlow(
    viewModel: AuthViewModel,
    onFinish: () -> Unit
) {
    var step by remember { mutableStateOf(0) }

    val gender = remember { mutableStateOf("") }
    val age = remember { mutableStateOf("") }
    val height = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    val activity = remember { mutableStateOf("") }
    val goal = remember { mutableStateOf("") }

    when (step) {
        0 -> UserInfoScreen(
            gender = gender.value,
            age = age.value,
            height = height.value,
            weight = weight.value,
            onGenderChange = { gender.value = it },
            onAgeChange = { age.value = it },
            onHeightChange = { height.value = it },
            onWeightChange = { weight.value = it },
            onNext = { step++ }
        )
        1 -> ActivityLevelScreen(
            selected = activity.value,
            onSelect = { activity.value = it },
            onNext = { step++ }
        )
        2 -> GoalSelectionScreen(
            selected = goal.value,
            onSelect = { goal.value = it },
            onSetupFinish = {
                val userInitial = UserInitial(
                    userId = CurrentUser.user?.id ?: "",
                    edad = age.value.toIntOrNull() ?: 0,
                    altura = height.value.toFloatOrNull() ?: 0f,
                    pesoInicial = weight.value.toFloatOrNull() ?: 0f,
                    genero = gender.value
                )

                val userTarget = UserTarget(
                    id = 1,
                    targetWeight = weight.value.toDoubleOrNull() ?: 0.0,
                    priority = when (goal.value) {
                        "Bajar de peso" -> UserTarget.Priority.Weight
                        "Ganar masa muscular" -> UserTarget.Priority.Muscle
                        else -> UserTarget.Priority.Health
                    }
                )

                val userPlan = UserPlan(
                    userId = CurrentUser.user?.id ?: "",
                    tmb = 1600f,
                    energia = 2000f,
                    proteinas = 100f,
                    grasas = 70f,
                    agua = 2.5f
                )

//                viewModel.signup(
//                    name = CurrentUser.user?.name ?: "",
//                    email = CurrentUser.user?.email ?: "",
//                    password = "temporal",
//                    userInitial = userInitial,
//                    userTarget = userTarget,
//                    userPlan = userPlan,
//                    onResult = { success, _ ->
//                        if (success) {
//                            onFinish() // Te lleva a Main.name después del onboarding
//                        }
//                    }
//                )
            }
        )
    }
}

@Composable
fun UserInfoScreen(
    gender: String,
    age: String,
    height: String,
    weight: String,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Hablemos sobre ti", style = MaterialTheme.typography.headlineSmall)
        DropdownMenuBox("Género", gender, listOf("Masculino", "Femenino", "Otro"), onGenderChange)
        OutlinedTextField(value = age, onValueChange = { onAgeChange(it) }, label = { Text("Edad") })
        OutlinedTextField(value = height, onValueChange = { onHeightChange(it) }, label = { Text("Altura (cm)") })
        OutlinedTextField(value = weight, onValueChange = { onWeightChange(it) }, label = { Text("Peso (kg)") })
        Button(onClick = onNext, modifier = Modifier.align(Alignment.End)) { Text("Siguiente") }
    }
}

@Composable
fun ActivityLevelScreen(
    selected: String,
    onSelect: (String) -> Unit,
    onNext: () -> Unit
) {
    val options = listOf("Sedentario", "Moderado", "Activo", "Muy activo")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("¿Cuál es tu nivel de actividad?", style = MaterialTheme.typography.headlineSmall)
        options.forEach {
            Button(
                onClick = { onSelect(it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == it) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) { Text(it) }
        }
        Button(onClick = onNext, modifier = Modifier.align(Alignment.End)) { Text("Siguiente") }
    }
}

@Composable
fun GoalSelectionScreen(
    selected: String,
    onSelect: (String) -> Unit,
    onSetupFinish: () -> Unit
) {
    val goals = listOf("Bajar de peso", "Tonificar", "Ganar masa muscular", "Mejorar hábitos")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("¿Cuál es tu objetivo?", style = MaterialTheme.typography.headlineSmall)
        goals.forEach {
            Button(
                onClick = { onSelect(it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == it) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) { Text(it) }
        }
        Button(onClick = onSetupFinish, modifier = Modifier.align(Alignment.End)) { Text("Finalizar") }
    }
}

@Composable
fun DropdownMenuBox(label: String, value: String, options: List<String>, onValueSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Box(modifier = Modifier.fillMaxWidth().background(Color.LightGray, RoundedCornerShape(8.dp)).padding(12.dp)) {
            Text(text = if (value.isEmpty()) "Selecciona..." else value)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = {
                        onValueSelected(it)
                        expanded = false
                    })
                }
            }
        }
    }
}