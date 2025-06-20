package com.unmsm.nutrihealth.logic

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.unmsm.nutrihealth.data.model.UserTarget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountSetupViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AccountSetupUiState())

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

    }

    fun submitTarget(): Unit {

    }

    fun calcPlan(): Unit {

    }

    fun confirm(): Unit {

    }
}