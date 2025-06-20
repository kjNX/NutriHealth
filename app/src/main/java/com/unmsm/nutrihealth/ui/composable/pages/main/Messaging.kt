package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Contact

// Definir color verde claro
val LightGreen = Color(0xFF81C784)

@Composable
fun ContactList(
    onSelect: (Contact) -> Unit,
    contacts: List<Contact>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(contacts) { contact ->
            ContactListItem(contact = contact, onClick = { onSelect(contact) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ContactListItem(
    onClick: () -> Unit,
    contact: Contact,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(12.dp)
            .background(LightGreen.copy(alpha = 0.1f)) // Fondo verde claro suave
            .clip(RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con inicial
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LightGreen), // Avatar en verde claro
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = contact.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ResourceCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(LightGreen.copy(alpha = 0.05f)), // Fondo verde claro suave en la tarjeta
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ir",
                tint = LightGreen // Ícono de flecha en verde claro
            )
        }
    }
}

// Vista previa de la lista de contactos
@Preview(showBackground = true)
@Composable
fun PreviewContactList() {
    val sampleContacts = listOf(
        Contact(name = "Juan Pérez", description = "Desarrollador de Software"),
        Contact(name = "María López", description = "Diseñadora UX/UI"),
        Contact(name = "Carlos García", description = "Gerente de Producto")
    )
    ContactList(onSelect = {}, contacts = sampleContacts)
}

// Vista previa de la tarjeta de recursos
@Preview(showBackground = true)
@Composable
fun PreviewResourceCard() {
    ResourceCard(
        title = "Recurso Educativo",
        description = "Una descripción detallada del recurso educativo disponible."
    )
}
