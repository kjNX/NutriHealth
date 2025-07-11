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
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random


fun generateSampleFoodData(): List<Food> {
    val foodNames = listOf(
        "Apple", "Banana", "Chicken Breast", "Broccoli", "Rice",
        "Salmon", "Bread", "Eggs", "Milk", "Cheese",
        "Spinach", "Potatoes", "Yogurt", "Orange", "Beef Steak",
        "Carrots", "Pasta", "Tuna", "Avocado", "Oats",
        "Cucumber", "Tomatoes", "Onions", "Pork Chop", "Lettuce",
        "Grapes", "Sweet Potato", "Bell Pepper", "Mushrooms", "Shrimp"
    )

    val foodList = mutableListOf<Food>()
    val fiveDaysAgo = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5))

    for (i in 1..30) {
        val name = foodNames[Random.nextInt(foodNames.size)]
        val energy = Random.nextDouble(50.0, 500.0) // kcals
        val protein = Random.nextDouble(0.0, 30.0) // grams
        val fats = Random.nextDouble(0.0, 25.0) // grams
        val water = Random.nextDouble(10.0, 90.0) // grams

        // Generate a timestamp within the last 5 days
        val randomTimeOffset = Random.nextLong(0, System.currentTimeMillis() - fiveDaysAgo.time)
        val timestamp = Date(fiveDaysAgo.time + randomTimeOffset)

        foodList.add(Food(name, energy, protein, fats, water, timestamp))
    }
    return foodList.toList()
}

@Composable
fun Composite(
    state: PagerState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onContactSelect: (Contact) -> Unit
) {
    HorizontalPager(state = state, modifier = modifier) { page ->
        when (page) {
            0 -> StartDisplay(
                foodList = generateSampleFoodData(),
                runList = listOf(
                    
                ),
                extraWater = 1200,
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


        if (showDialog) {
            TypeAddDialog(
                viewModel = foodViewModel,
                onDismiss = { showDialog = false },
                onConfirm = { food ->
                    foodViewModel.addFood(food) { success ->
                        showDialog = false
                    }
                }
            )
        }


    }
}

