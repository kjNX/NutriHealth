// ✅ ActivityHistoryViewModel.kt
package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.model.User
import java.util.*

class ActivityHistoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveRunToFirestore(run: Run, onResult: (Boolean, String) -> Unit) {
        val activity = mapOf(
            "timestamp" to run.timestamp,
            "avgSpeedInKMH" to run.avgSpeedInKMH,
            "distanceInMeters" to run.distanceInMeters,
            "durationInMillis" to run.durationInMillis,
            "caloriesBurned" to run.caloriesBurned
        )

        firestore.collection("users")
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
}
