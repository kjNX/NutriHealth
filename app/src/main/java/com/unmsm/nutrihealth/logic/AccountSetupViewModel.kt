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

    fun submitData(): Unit {
        Log.d("FirestoreDebug", "📤 submitData() con User.id = ${User.id}")
        UserData.age = _uiState.value.age.toIntOrNull() ?: 0
        UserData.height = _uiState.value.height.toIntOrNull() ?: 0
        UserData.weight = _uiState.value.weight.toFloatOrNull() ?: 0f
        UserData.gender = if(_uiState.value.genderIndex == 0) UserData.Gender.Male else UserData.Gender.Female
        firestore.collection("user")
            .document(User.id)
            .collection("setup_data")
            .document("base_data")
            .set(UserData)
            .addOnSuccessListener {
                Log.d("FirestoreDebug", "✅ base_data guardado correctamente para ${User.id}")
            }
            .addOnFailureListener {
                Log.e("FirestoreDebug", "❌ Error guardando base_data: ${it.message}")
            }

        advanceStage()
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
        _uiState.update {
            it.copy(
                tmb = UserObjective.tmb,
                recommendedKcal = UserObjective.dailyCal,
                protein = UserObjective.protein,
                carbs = UserObjective.carbs,
                fats = UserObjective.fats
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