package com.unmsm.nutrihealth.ui

import kotlinx.coroutines.flow.MutableStateFlow

class NViewModel {
    private var _uiState = MutableStateFlow(NUiState())
}