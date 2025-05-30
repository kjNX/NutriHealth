package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Contact
import com.unmsm.nutrihealth.data.model.Message
import com.unmsm.nutrihealth.logic.ChatViewModel
import com.unmsm.nutrihealth.ui.composable.blocks.SubsectionTopBar
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Messaging(contact: Contact, onNavigate: () -> Unit, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = { SubsectionTopBar(title = contact.name, onNavigate = onNavigate) },
        bottomBar = {
            MessageBar(
                message = userInput,
                onMessageChange = { userInput = it },
                onSend = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendMessage(userInput)
                        userInput = ""
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                MessageItem(
                    contact = contact,
                    message = Message(msg.content, msg.timestamp, msg.isUser)
                )
            }
        }
    }
}


@Composable
fun MessageItem(contact: Contact, message: Message, modifier: Modifier = Modifier) {
    val isUser = message.isOwned
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Avatar del contacto
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = message.time,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MessageLog(contact: Contact, messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        items(messages) { message ->
            MessageItem(contact = contact, message = message)
        }
    }
}

@Composable
fun MessageBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Acción de voz opcional */ }) {
            Icon(imageVector = Icons.Default.KeyboardVoice, contentDescription = "Voice")
        }
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        IconButton(onClick = onSend) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
        }
    }
}


@Preview
@Composable
private fun MessagingPreview() {
    val contact = Contact("ML", "")

    NutriHealthTheme {
        Messaging(contact,onNavigate = {})
    }
}
