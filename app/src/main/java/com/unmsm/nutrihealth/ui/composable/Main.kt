package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.EntryFABs
import com.unmsm.nutrihealth.ui.composable.blocks.MainTopBar
import com.unmsm.nutrihealth.ui.composable.blocks.NavBar
import com.unmsm.nutrihealth.ui.composable.pages.main.ContactList
import com.unmsm.nutrihealth.ui.composable.pages.main.StartDisplay
import com.unmsm.nutrihealth.ui.composable.pages.main.TrackingDisplay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.model.Food

@Composable
fun Composite(state: PagerState, modifier: Modifier = Modifier, onContactSelect: (Contact) -> Unit) {
    HorizontalPager(state = state, modifier = modifier) { page ->
        when(page) {
            0 -> StartDisplay(modifier = Modifier.fillMaxSize())
            1 -> ContactList(
                contacts = getContacts(),
                modifier = Modifier.fillMaxSize(),
                onSelect = onContactSelect
            )
            2 -> TrackingDisplay(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MainDisplay(
    onTopBarClick: List<() -> Unit>,
    onScanClick: () -> Unit,
    onContactSelect: (Contact) -> Unit,
    viewModel: FoodViewModel = viewModel()
) {
    val pagerState = rememberPagerState { 3 }
    var showDialog by remember { mutableStateOf(false) }

    val hideDialog = { showDialog = false }

    Scaffold(
        topBar = { MainTopBar(onTopBarClick) },
        bottomBar = { NavBar(pagerState) },
        floatingActionButton = {
            if (pagerState.currentPage != 2) {
                EntryFABs(
                    onScanClick = onScanClick,
                    onTypeClick = { showDialog = true }
                )
            }
        }
    ) { innerPadding ->
        Composite(
            state = pagerState,
            onContactSelect = onContactSelect,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

        if (showDialog) {
            TypeAddDialog(
                onDismiss = hideDialog,
                onCancel = hideDialog,
                onConfirm = { food: Food ->
                    viewModel.addFood(food) { success ->
                        // Aquí podrías mostrar un Toast/Snackbar si quieres
                        showDialog = false
                    }
                }
            )
        }
    }
}

