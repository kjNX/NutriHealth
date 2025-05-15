package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Section(
    val title: String,
    val tabsTitle: List<String>,
    val tabsContent: List<@Composable () -> Unit>
)

@Composable
fun FitCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun MenuTab(
    selected: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                color = if (selected) Color.White else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun TabbedMenu(
    idx: Int,
    labels: List<String>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = Color.LightGray,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 3.dp, horizontal = 1.dp)
    ) {
        for((i, label) in labels.withIndex())
            MenuTab(
                selected = i == idx,
                title = label,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp),
                onClick = { onClick(i) }
            )
    }
}

@Composable
fun TabbedHeader(
    section: Section,
    idx: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(text = section.title, style = MaterialTheme.typography.titleLarge)
        TabbedMenu(
            idx = idx,
            labels = section.tabsTitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = onClick
        )
    }
}

@Composable
fun TabbedLayout(section: Section, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState { section.tabsContent.size }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier) {
        TabbedHeader(
            section = section,
            idx = pagerState.currentPage,
            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } }
        )
        HorizontalPager(state = pagerState) { page ->
            section.tabsContent[page]()
        }
    }
}
