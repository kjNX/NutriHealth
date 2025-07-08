package com.unmsm.nutrihealth.logic

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
    fun setAge(i: String) = {
        if(i == "" || i.toIntOrNull() != null) _uiState.update { it.copy(age = i) }
    }
    fun setHeight(i: String) = {
        if(i == "" || i.toIntOrNull() != null) _uiState.update { it.copy(height = i) }
    }
    fun setWeight(i: String) = {
        if(i == "" || i.toFloatOrNull() != null) _uiState.update { it.copy(weight = i) }
    }
    fun setTargetWeight(i: String) = {
        if(i == "" || i.toFloatOrNull() != null) _uiState.update { it.copy(targetWeight = i) }
    }
    fun setMainGoal(i: UserTarget.Priority) = _uiState.update { it.copy(mainGoal = i) }

    fun submitData(): Unit {
        UserData.age = _uiState.value.age.toIntOrNull() ?: 0
        UserData.height = _uiState.value.height.toIntOrNull() ?: 0
        UserData.weight = _uiState.value.weight.toFloatOrNull() ?: 0f
        UserData.gender = if(_uiState.value.genderIndex == 0) UserData.Gender.Male else UserData.Gender.Female
        firestore.collection("users")
            .document(User.id)
            .collection("setup_data")
            .document("base_data")
            .set(UserData)

        advanceStage()
    }

    fun submitTarget(): Unit {
        UserTarget.targetWeight = _uiState.value.targetWeight.toFloatOrNull() ?: 0f
        UserTarget.priority = _uiState.value.mainGoal
        firestore.collection("users")
            .document(User.id)
            .collection("setup_data")
            .document("target_data")
            .set(UserTarget)

        calcPlan()
        advanceStage()
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
        firestore.collection("users")
            .document(User.id)
            .collection("setup_data")
            .document("objective_data")
            .set(UserObjective)
        advanceStage()
    }

    fun advanceStage(): Unit {
        ++User.stage
        firestore.collection("users")
            .document(User.id)
            .update("stage", User.stage)
    }
}