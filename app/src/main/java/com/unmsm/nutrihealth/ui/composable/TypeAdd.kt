package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.unmsm.nutrihealth.data.model.Food
import androidx.compose.runtime.*
import com.unmsm.nutrihealth.logic.FoodViewModel
import java.util.Date
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeAddDialog(
    viewModel: FoodViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Food) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    var energy by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }

    val suggestions by viewModel.foodSuggestions.collectAsState()
    val filteredSuggestions = suggestions.filter {
        it.name.contains(query, ignoreCase = true)
    }
    val otros = mutableListOf<Food>()
    val agrupados = mutableMapOf<String, MutableList<Food>>()

    for (food in filteredSuggestions) {
        val nameLower = food.name.lowercase()

        val categoria = when {
            listOf("arroz", "chaufa", "arabe").any { nameLower.contains(it) } -> "🍚 Arroces"
            listOf("pollo", "gallina").any { nameLower.contains(it) } -> "🍗 Platos con Pollo"
            listOf("sopa", "caldo", "menestr", "aguadito").any { nameLower.contains(it) } -> "🥣 Sopas"
            listOf("ensalada", "vegetal", "palta").any { nameLower.contains(it) } -> "🥗 Ensaladas"
            listOf("ají", "rocoto", "picante").any { nameLower.contains(it) } -> "🌶️ Ajíes"
            listOf("pescado", "sudado", "cebiche", "trucha").any { nameLower.contains(it) } -> "🐟 Pescados"
            listOf("cerdo", "chancho").any { nameLower.contains(it) } -> "🐖 Cerdo"
            else -> null
        }

        if (categoria != null) {
            agrupados.getOrPut(categoria) { mutableListOf() }.add(food)
        } else {
            otros.add(food)
        }
    }

// ✅ Mapa final con "Otros" al final si hay elementos no clasificados
    val groupedSuggestions: Map<String, List<Food>> =
        if (otros.isNotEmpty()) agrupados + mapOf("🍽️ Otros" to otros)
        else agrupados



    LaunchedEffect(Unit) {
        viewModel.loadGlobalFoodSuggestions()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🍴 Agregar comida") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize()
        ) {

            // 🔍 Buscador
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    selectedFood = null
                },
                label = { Text("Buscar comida peruana") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📋 Resultados
            if (groupedSuggestions.isNotEmpty() && selectedFood == null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    groupedSuggestions.forEach { (categoria, platos) ->
                        item {
                            Text(
                                text = categoria,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                        items(platos.take(4)) { food ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedFood = food
                                        query = food.name
                                        energy = food.energy.toString()
                                        protein = food.protein.toString()
                                        fat = food.fats.toString()
                                        water = food.water.toString()
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(food.name, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "Kcal: ${food.energy} | Prot: ${food.protein}g | Grasas: ${food.fats}g",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 🧪 Nutrientes editables (solo si se ha seleccionado un alimento)
            if (selectedFood != null ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("🔬 Información nutricional", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                NutrientField("🔥 Energía (kcal)", energy) { energy = it }
                NutrientField("💪 Proteínas (g)", protein) { protein = it }
                NutrientField("🧈 Grasas (g)", fat) { fat = it }
                NutrientField("💧 Agua (%)", water) { water = it }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val food = Food(
                            name = query,
                            energy = energy.toDoubleOrNull() ?: 0.0,
                            protein = protein.toDoubleOrNull() ?: 0.0,
                            fats = fat.toDoubleOrNull() ?: 0.0,
                            water = water.toDoubleOrNull() ?: 0.0,
                            timestamp = Date()
                        )
                        onConfirm(food)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✅ Guardar comida")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        // 🧼 Volver al buscador: limpia selección y campos
                        selectedFood = null
                        query = ""
                        energy = ""
                        protein = ""
                        fat = ""
                        water = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔙 Volver al buscador")
                }

            }
        }
    }
}
@Composable
fun NutrientField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

