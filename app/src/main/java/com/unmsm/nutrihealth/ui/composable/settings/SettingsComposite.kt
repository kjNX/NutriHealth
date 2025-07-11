package com.unmsm.nutrihealth.ui.composable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

val nameList = listOf("Cuenta", "Aplicación")
val iconList = listOf(Icons.Default.Person, Icons.Default.Info)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComposite(
    updateEnabled: Boolean,
    exitEnabled: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
//    phoneNumber: String,
//    onPhoneNumberChange: (String) -> Unit,
    onCommit: () -> Unit,
    onPasswordChangeRequest: () -> Unit,
    measureType: Boolean,
    onMeasureTypeToggle: (Boolean) -> Unit,
    notifications: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
//    onExportRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pagerState = rememberPagerState { nameList.size }
    var coroutineScope = rememberCoroutineScope()
    val tabList = listOf(
        @Composable { modifier: Modifier ->
            AccountPage(
                updateEnabled = updateEnabled,
                exitEnabled = exitEnabled,
                name = name,
                onNameChange = onNameChange,
                email = email,
                onEmailChange = onEmailChange,
//            phoneNumber = phoneNumber,
//            onPhoneNumberChange = onPhoneNumberChange,
                onCommit = onCommit,
                onPasswordChangeRequest = onPasswordChangeRequest,
                modifier = modifier
            )
        },
        @Composable { modifier: Modifier ->
            AppliPage(
                exitEnabled = exitEnabled,
                measureType = measureType,
                onMeasureTypeToggle = onMeasureTypeToggle,
                notifications = notifications,
                onNotificationsToggle = onNotificationsToggle,
//            onExportRequest = onExportRequest,
                onDeleteRequest = onDeleteRequest,
                onLogout = onLogout,
                modifier = modifier
            )
        }
    )

    Column(modifier = modifier) {
        PrimaryTabRow(pagerState.currentPage) {
            for (i in 0..<tabList.size)
                Tab(
                    selected = i == pagerState.currentPage,
                    onClick = {
                        if (i != pagerState.currentPage) coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                i
                            )
                        }
                    },
                    text = { Text(text = nameList[i]) },
                    icon = { Icon(imageVector = iconList[i], contentDescription = null) }
                )
        }
        HorizontalPager(state = pagerState)
        { tabList[it](Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) }
    }
}
