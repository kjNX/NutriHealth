package com.unmsm.nutrihealth.logic

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountSetupViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AccountSetupUiState())

    val uiState = _uiState.asStateFlow()

    fun submitData(): Unit {

    }

    fun submitTarget(): Unit {

    }

    fun confirm(): Unit {

    }
}