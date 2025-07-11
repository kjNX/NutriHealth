package com.unmsm.nutrihealth.ui.composable.pages.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.data.model.Contact

@Composable
fun ContactList(
    contacts: List<Contact>,
    modifier: Modifier = Modifier,
    onSelect: (Contact) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Asesores Expertos",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Selecciona un experto para recibir asesoría personalizada",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(contacts) { contact ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(contact) },
                    colors = CardDefaults.cardColors(
                        containerColor = when(contact.name) {
                            "Asesor de Bienestar" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            "Nutrición" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    when(contact.name) {
                                        "Asesor de Bienestar" -> MaterialTheme.colorScheme.primary
                                        "Nutrición" -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.first().toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            
                            Text(
                                text = when(contact.name) {
                                    "Asesor de Bienestar" -> "Recomendaciones personalizadas de salud y bienestar"
                                    "Nutrición" -> "Planificación de dieta y asesoría nutricional"
                                    else -> "Planes de entrenamiento personalizados"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
} 