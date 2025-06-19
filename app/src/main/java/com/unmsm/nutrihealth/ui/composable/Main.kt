package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.EntryFABs
import com.unmsm.nutrihealth.ui.composable.blocks.MainTopBar
import com.unmsm.nutrihealth.ui.composable.blocks.NavBar
import com.unmsm.nutrihealth.ui.composable.pages.main.ContactList
import com.unmsm.nutrihealth.ui.composable.pages.main.StartDisplay
import com.unmsm.nutrihealth.ui.composable.pages.map.CurrentRunScreen

@Composable
fun Composite(
    state: PagerState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onContactSelect: (Contact) -> Unit
) {
    HorizontalPager(state = state, modifier = modifier) { page ->
        when (page) {
            0 -> StartDisplay(modifier = Modifier.fillMaxSize())
            1 -> ContactList(
                contacts = getContacts(),
                modifier = Modifier.fillMaxSize(),
                onSelect = onContactSelect
            )
            2 -> CurrentRunScreen(navController = navController)
        }
    }
}

@Composable
fun MainDisplay(
    navController: NavController,
    onTopBarClick: List<() -> Unit>,
    onScanClick: () -> Unit,
    onContactSelect: (Contact) -> Unit,
    viewModel: FoodViewModel = viewModel()
) {
    val pagerState = rememberPagerState { 3 } // 3 páginas: Inicio, Chat, Actividades
    var showDialog by remember { mutableStateOf(false) }

    val hideDialog = { showDialog = false }

    Scaffold(
        topBar = { MainTopBar(onTopBarClick) },
        bottomBar = { NavBar(pagerState) }, // usa scrollToPage para navegar
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
            navController = navController,
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
                    viewModel.addFood(food) {
                        showDialog = false
                    }
                }
            )
        }
    }
}
