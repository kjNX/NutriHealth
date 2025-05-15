package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.unmsm.nutrihealth.data.repository.getContacts
import com.unmsm.nutrihealth.data.repository.trackerStats
import com.unmsm.nutrihealth.ui.composable.blocks.EntryFABs
import com.unmsm.nutrihealth.ui.composable.blocks.MainTopBar
import com.unmsm.nutrihealth.ui.composable.blocks.NavBar
import com.unmsm.nutrihealth.ui.composable.pages.ContactList
import com.unmsm.nutrihealth.ui.composable.pages.StartDisplay
import com.unmsm.nutrihealth.ui.composable.pages.TrackingDisplay
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

@Composable
fun Composite(state: PagerState, modifier: Modifier = Modifier) {
    HorizontalPager(state = state, modifier = modifier) { page ->
        when(page) {
            0 -> StartDisplay(modifier = Modifier.fillMaxSize())
            1 -> ContactList(contacts = getContacts(), modifier = Modifier.fillMaxSize())
            2 -> TrackingDisplay(stats = trackerStats(), modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MainDisplay() {
    var pagerState = rememberPagerState{ 3 }

    Scaffold(
        topBar = { MainTopBar() },
        bottomBar = { NavBar(pagerState) },
        floatingActionButton = { EntryFABs() }
    ) { innerPadding ->
        Composite(state = pagerState, modifier = Modifier.fillMaxSize().padding(innerPadding))
    }
}

@Preview
@Composable
private fun Preview() {
    NutriHealthTheme {
        MainDisplay()
    }
}