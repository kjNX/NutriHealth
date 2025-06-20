package com.unmsm.nutrihealth.ui.composable.pages.map

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.logic.ActivityHistoryViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navController: NavController, historyViewModel: ActivityHistoryViewModel = viewModel()) {
    // Estado para almacenar las carreras obtenidas
    val runs = remember { mutableStateOf<List<Run>>(emptyList()) }
    val errorMessage = remember { mutableStateOf("") }

    // Obtener las carreras de Firestore
    LaunchedEffect(true) {
        historyViewModel.fetchRunsFromFirestore { retrievedRuns, message ->
            runs.value = retrievedRuns.sortedByDescending { it.timestamp } // Ordena las carreras por fecha, más recientes primero
            errorMessage.value = message
        }
    }

    // Mostrar el error si no se pudo obtener las carreras
    if (errorMessage.value.isNotEmpty()) {
        Toast.makeText(LocalContext.current, errorMessage.value, Toast.LENGTH_SHORT).show()
    }

    // Formateador de fecha
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    // Usamos Column para colocar la barra de navegación encima de la lista
    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de navegación para volver
        SubsectionTopBar(
            title = "Historial de Carreras",  // Título de la pantalla
            onNavigate = {
                navController.popBackStack() // Volver a la pantalla anterior
            },
            modifier = Modifier.padding(top = 8.dp) // Espaciado de la barra de navegación
        )

        // Mostrar las carreras en una lista, con un espaciado superior
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)) {  // Añadimos padding en la parte superior
            items(runs.value) { run ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Distancia: ${run.distanceInMeters / 1000} km") // Convierte metros a kilómetros
                        Text(text = "Duración: ${run.durationInMillis / 1000 / 60} min") // Convierte milisegundos a minutos
                        Text(text = "Fecha: ${dateFormatter.format(run.timestamp)}") // Muestra la fecha de la carrera con formato legible
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(navController = rememberNavController())
}
