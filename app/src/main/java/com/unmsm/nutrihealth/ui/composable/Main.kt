package com.unmsm.nutrihealth.ui.composable

import TypeAddDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.Run
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.logic.FoodViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.EntryFABs
import com.unmsm.nutrihealth.ui.composable.blocks.MainTopBar
import com.unmsm.nutrihealth.ui.composable.blocks.NavBar
import com.unmsm.nutrihealth.ui.composable.pages.main.ContactList
import com.unmsm.nutrihealth.ui.composable.pages.main.StartDisplay
import com.unmsm.nutrihealth.ui.composable.pages.map.CurrentRunScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

suspend fun getFoods(): List<Food> {
    val database = FirebaseFirestore.getInstance()
    val collection = database.collection("user/${User.id}/foods").get().await()
    val foods = mutableListOf<Food>()
    for (document in collection.documents) {
        val food = document.toObject(Food::class.java)
        if (food != null) foods.add(food)
    }
    return foods
}

suspend fun getRun(): List<Run> {
    val database = FirebaseFirestore.getInstance()
    val collection = database.collection("user/${User.id}/activities").get().await()
    val runs = mutableListOf<Run>()
    for (document in collection.documents) {
        val run = document.toObject(Run::class.java)
        if (run != null) runs.add(run)
    }
    return runs
}

@Composable
fun Composite(
    state: PagerState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onContactSelect: (Contact) -> Unit
) {
    var foodList by remember { mutableStateOf(listOf<Food>()) }
    var runList by remember { mutableStateOf(listOf<Run>()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            foodList = getFoods()
            runList = getRun()
        }
    }

    HorizontalPager(state = state, modifier = modifier) { page ->
        when (page) {
            0 -> StartDisplay(
                foodList = foodList,
                runList = runList,
                modifier = Modifier.fillMaxSize())
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
            navController = navController,
            onContactSelect = onContactSelect,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )



        val foodViewModel: FoodViewModel = viewModel()


        // Dentro de tu Composable principal
        if (showDialog) {
            TypeAddDialog(
                viewModel = foodViewModel,
                onDismiss = {
                    showDialog = false
                },
                onConfirm = { food ->
                    // Guardar comida en Firestore (o localmente si usas Room también)
                    foodViewModel.addFood(food) { success ->
                        if (success) {
                            showDialog = false
                        } else {
                            // Opcional: Mostrar mensaje de error o retry
                            // e.g., Snackbar("No se pudo guardar")
                        }
                    }
                }
            )
        }



    }
}

