package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTopBar(contact: Contact, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(text = contact.displayName, style = MaterialTheme.typography.headlineMedium)
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        modifier = modifier
    )
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
        modifier = modifier.fillMaxWidth().padding(8.dp),
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

@Composable
fun ContactList(contacts: List<Contact>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(contacts) { contact ->
            ContactListItem(contact = contact)
        }
    }
}

@Composable
fun ContactListItem(contact: Contact, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable(onClick = {})
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = contact.displayName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = contact.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ResourceCard(title: String, description: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.labelMedium)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SupportPreview() {
    val contact = Contact("ML", "")

//    ContactList(contacts = listOf(Contact("ML", "Smart coach"), Contact("Ana", "Nutricionista")))
    NutriHealthTheme {
        Scaffold(topBar = { MessageTopBar(contact) }, bottomBar = { MessageBar() }) { innerPadding ->
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