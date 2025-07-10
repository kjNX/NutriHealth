package com.unmsm.nutrihealth.logic

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@HiltViewModel
class TargetViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    var currentWeight by mutableStateOf("")
        private set
    var targetWeight by mutableStateOf("")
        private set
    var progress by mutableStateOf(0f)
        private set

    init {
        loadTargetData()
    }

    private fun loadTargetData() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("user")
            .document(userId)
            .collection("setup_data")
            .document("target_data")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val current = document.getDouble("currentWeight") ?: 0.0
                    val target = document.getDouble("targetWeight") ?: 0.0

                    currentWeight = "$current kg"
                    targetWeight = "$target kg"

                    progress = if (current == target || target == 0.0) 1f
                    else ((current - target) / (current)).toFloat().coerceIn(0f, 1f)

                }
            }
    }
    private fun calculateProgress(current: String, target: String): Float {
        val curr = current.toFloatOrNull()
        val targ = target.toFloatOrNull()
        if (curr != null && targ != null && curr > targ) {
            val lost = curr - targ
            val total = curr - targ
            return (lost / total).coerceIn(0f, 1f)
        }
        return 0f
    }
}
