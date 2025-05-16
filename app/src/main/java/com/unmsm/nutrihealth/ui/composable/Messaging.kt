package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.model.Message
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

@Composable
fun Messaging(onNavigate: () -> Unit) {
    val contact = Contact("ML", "")
    Scaffold(
        topBar = { SubsectionTopBar(title = contact.displayName, onNavigate = onNavigate) },
        bottomBar = { MessageBar() }
    ) { innerPadding ->
        MessageLog(
            contact = contact,
            messages = listOf(
                Message("Hello!", "now", true),
                Message("Good evening!", "later", false)
            ),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MessageItem(contact: Contact, message: Message, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = if (message.isOwned) "You" else contact.displayName,
            color = if (message.isOwned) Color.Red else Color.Green,
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = message.content)
        Text(text = message.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun MessageLog(contact: Contact, messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(messages) { message ->
            MessageItem(contact = contact, message = message)
        }
    }
}

@Composable
fun MessageBar(modifier: Modifier = Modifier) {
    var message by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {}) {
            Icon(imageVector = Icons.Default.KeyboardVoice, contentDescription = "Voice message")
        }
        TextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
        Button(onClick = {}) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
        }
    }
}

@Preview
@Composable
private fun MessagingPreview() {
    val contact = Contact("ML", "")

    NutriHealthTheme {
        Scaffold(
            topBar = { SubsectionTopBar(contact.displayName, {}) },
            bottomBar = { MessageBar() }
        ) { innerPadding ->
            MessageLog(
                contact = contact,
                messages = listOf(
                    Message("Hello!", "now", true),
                    Message("Good evening!", "later", false)
                ),
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
