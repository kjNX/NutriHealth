import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.logic.FoodViewModel
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
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }

    for (food in filteredSuggestions) {
        val nameLower = food.name.lowercase()

        val categoria = when {
            listOf("arroz", "chaufa", "arabe").any { nameLower.startsWith(it) } -> "🍚 Arroces"
            listOf("pollo", "gallina").any { nameLower.startsWith(it) } -> "🍗 Platos con Pollo"
            listOf("sopa", "caldo", "menestr", "aguadito").any { nameLower.startsWith(it) } -> "🥣 Sopas"
            listOf("ensalada", "vegetal", "palta").any { nameLower.startsWith(it) } -> "🥗 Ensaladas"
            listOf("ají", "rocoto", "picante").any { nameLower.startsWith(it) } -> "🌶️ Ajíes"
            listOf("pescado", "sudado", "cebiche", "trucha").any { nameLower.startsWith(it) } -> "🐟 Pescados"
            listOf("cerdo", "chancho","seco","cabrito","bisteck").any { nameLower.startsWith(it) } -> "🐖 Carnes"
            else -> null
        }

        if (categoria != null) {
            agrupados.getOrPut(categoria) { mutableListOf() }.add(food)
        } else {
            otros.add(food)
        }
    }

    val groupedSuggestions = if (otros.isNotEmpty()) agrupados + mapOf("🍽️ Otros" to otros) else agrupados

    LaunchedEffect(Unit) {
        viewModel.loadGlobalFoodSuggestions()
    }

    Scaffold   ( modifier = Modifier
            .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🍴 Agregar comida", color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.background) // 👈 también aquí
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    selectedFood = null
                },
                label = { Text("Buscar comida peruana") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (groupedSuggestions.isNotEmpty() && selectedFood == null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    groupedSuggestions.forEach { (categoria, platos) ->
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedCategories[categoria] = !(expandedCategories[categoria] ?: false)
                                        }
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = categoria,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = if (expandedCategories[categoria] == true) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (expandedCategories[categoria] == true) {
                                    platos.forEach { food ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                                .clickable {
                                                    selectedFood = food
                                                    query = food.name
                                                    energy = food.energy.toString()
                                                    protein = food.protein.toString()
                                                    fat = food.fats.toString()
                                                    water = food.water.toString()
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                        ) {
                                            Column(Modifier.padding(12.dp)) {
                                                Text(
                                                    text = food.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Kcal: ${food.energy} kcal",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray
                                                )
                                                Text(
                                                    text = "Proteínas: ${food.protein}g | Grasas: ${food.fats}g",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedFood != null) {
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("✅ Guardar comida", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        selectedFood = null
                        query = ""
                        energy = ""
                        protein = ""
                        fat = ""
                        water = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔙 Volver al buscador", color = MaterialTheme.colorScheme.primary)
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