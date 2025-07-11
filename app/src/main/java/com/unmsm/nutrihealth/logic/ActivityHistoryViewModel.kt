package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.model.User
import java.util.*

class ActivityHistoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Método para guardar una carrera en Firestore
    fun saveRunToFirestore(run: Run, onResult: (Boolean, String) -> Unit) {
        val activity = mapOf(
            "timestamp" to Timestamp(run.timestamp), // Convertir Date a Timestamp
            "avgSpeedInKMH" to run.avgSpeedInKMH,
            "distanceInMeters" to run.distanceInMeters,
            "durationInMillis" to run.durationInMillis,
            "caloriesBurned" to run.caloriesBurned
        )

        firestore.collection("user")
            .document(User.id)
            .collection("activities")
            .add(activity)
            .addOnSuccessListener {
                onResult(true, "Guardado en Firestore")
            }
            .addOnFailureListener {
                onResult(false, "Error al guardar: ${it.message}")
            }
    }

    // Método para traer todas las carreras desde Firestore
    fun fetchRunsFromFirestore(onResult: (List<Run>, String) -> Unit) {
        firestore.collection("user")
            .document(User.id)
            .collection("activities")
            .get()
            .addOnSuccessListener { snapshot ->
                val runs = snapshot.documents.mapNotNull { document ->
                    try {
                        val timestamp = document.get("timestamp") as? Timestamp
                        val avgSpeedInKMH = document.getDouble("avgSpeedInKMH")?.toFloat() ?: 0f
                        val distanceInMeters = document.getLong("distanceInMeters")?.toInt() ?: 0
                        val durationInMillis = document.getLong("durationInMillis") ?: 0L
                        val caloriesBurned = document.getLong("caloriesBurned")?.toInt() ?: 0

                        Run(
                            timestamp = timestamp?.toDate() ?: Date(),
                            avgSpeedInKMH = avgSpeedInKMH,
                            distanceInMeters = distanceInMeters,
                            durationInMillis = durationInMillis,
                            caloriesBurned = caloriesBurned
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                onResult(runs, "Datos obtenidos correctamente de Firestore")
            }
            .addOnFailureListener {
                onResult(emptyList(), "Error al obtener datos: ${it.message}")
            }
    }
}
