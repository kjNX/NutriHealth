package com.unmsm.nutrihealth.logic

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.model.UserData
import com.unmsm.nutrihealth.data.model.UserObjective
import com.unmsm.nutrihealth.data.model.UserTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountSetupViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AccountSetupUiState())
    val firestore = Firebase.firestore

    val uiState = _uiState.asStateFlow()

    fun setGenderIndex(i: Int) = _uiState.update { it.copy(genderIndex = i) }
    fun setIntensity(i: Float) = _uiState.update { it.copy(intensity = i) }
    fun setAge(i: String) {
        if(i == "" || i.toIntOrNull() != null) {
            _uiState.update { it.copy(age = i) }
        }
    }

    fun setHeight(i: String) {
        if(i == "" || i.toIntOrNull() != null) {
            _uiState.update { it.copy(height = i) }
        }
    }

    fun setWeight(i: String) {
        if(i == "" || i.toFloatOrNull() != null) {
            _uiState.update { it.copy(weight = i) }
        }
    }

    fun setTargetWeight(i: String) {
        if(i == "" || i.toFloatOrNull() != null) {
            _uiState.update { it.copy(targetWeight = i) }
        }
    }

    fun setMainGoal(i: UserTarget.Priority) = _uiState.update { it.copy(mainGoal = i) }

    fun submitData(): Boolean {
        val edad = _uiState.value.age.toIntOrNull()
        val altura = _uiState.value.height.toIntOrNull()
        val peso = _uiState.value.weight.toFloatOrNull()
        val generoIndex = _uiState.value.genderIndex

        if (edad == null || edad !in 10..120) return false
        if (altura == null || altura !in 50..250) return false
        if (peso == null || peso !in 20f..300f) return false
        if (generoIndex != 0 && generoIndex != 1) return false

        // Si todo está bien, guarda
        UserData.age = edad
        UserData.height = altura
        UserData.weight = peso
        UserData.gender = if (generoIndex == 0) UserData.Gender.Male else UserData.Gender.Female

        firestore.collection("user")
            .document(User.id)
            .collection("setup_data")
            .document("base_data")
            .set(UserData)

        advanceStage()
        return true
    }


    fun submitTarget() {
        Log.d("FirestoreDebug", "📤 submitData() con User.id = ${User.id}")

        val userTarget = com.unmsm.nutrihealth.data.model.UserTarget(
            targetWeight = _uiState.value.targetWeight.toFloatOrNull()?.toDouble() ?: 0.0,
            priority = _uiState.value.mainGoal
        )

        firestore.collection("user")
            .document(User.id)
            .collection("setup_data")
            .document("target_data")
            .set(userTarget)
            .addOnSuccessListener {
                Log.d("FirestoreDebug", "✅ target_data guardado correctamente para ${User.id}")
                calcPlan()
                advanceStage()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al guardar target: ${it.message}")
            }
    }



    fun calcPlan(): Unit {
        UserObjective.tmb = ((10 * UserData.weight) + (6.25 * UserData.height) - (5 * UserData.age) + UserData.gender.bonus).toInt()
        UserObjective.dailyCal = (UserObjective.tmb * (1.2 + .7/8 * _uiState.value.intensity.toInt())).toInt()
        UserObjective.protein = (UserObjective.tmb * .3).toInt()
        UserObjective.carbs = (UserObjective.tmb * .45).toInt()
        UserObjective.fats = (UserObjective.tmb * .25).toInt()

        val pesoActual = UserData.weight
        val pesoMeta = _uiState.value.targetWeight.toFloatOrNull() ?: pesoActual
        val diferencia = kotlin.math.abs(pesoMeta - pesoActual)
        val semanas = (diferencia * 0.5).toInt().coerceIn(8, 12) // entre 2 a 3 meses

        _uiState.update {
            it.copy(
                tmb = UserObjective.tmb,
                recommendedKcal = UserObjective.dailyCal,
                protein = UserObjective.protein,
                carbs = UserObjective.carbs,
                fats = UserObjective.fats,
                timeToReach = semanas // ✅ Aquí se corrige
            )
        }
    }


    fun confirm(): Unit {
        Log.d("FirestoreDebug", "📤 confirm() con User.id = ${User.id}")

        firestore.collection("user")
            .document(User.id)
            .collection("setup_data")
            .document("objective_data")
            .set(UserObjective)
            .addOnSuccessListener {
            Log.d("FirestoreDebug", "✅ objective_data guardado correctamente para ${User.id}")
            }
            .addOnFailureListener {
                Log.e("FirestoreDebug", "❌ Error guardando objective_data: ${it.message}")
            }
        advanceStage()
    }

    fun advanceStage(): Unit {
        ++User.stage
        Log.d("FirestoreDebug", "📈 Avanzando stage a ${User.stage} para User.id = ${User.id}")

        firestore.collection("user")
            .document(User.id)
            .update("stage", User.stage)
            .addOnSuccessListener {
                Log.d("FirestoreDebug", "✅ stage actualizado correctamente para ${User.id}")
            }
            .addOnFailureListener {
                Log.e("FirestoreDebug", "❌ Error actualizando stage: ${it.message}")
            }
    }
}