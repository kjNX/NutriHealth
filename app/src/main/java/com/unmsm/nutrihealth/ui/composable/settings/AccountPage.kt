package com.unmsm.nutrihealth.ui.composable.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.unmsm.nutrihealth.ui.composable.EnhancedTextField

@Composable
fun TitledCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if(icon == null) Text(text = title, style = MaterialTheme.typography.titleMedium)
            else Row {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
fun LabeledButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        if(icon != null) Icon(imageVector = icon, contentDescription = null)
        Text(text = title)
    }
}

@Composable
fun IconLabel(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null
) {
    val textLabel = if(subtitle.isEmpty()) @Composable { modifier: Modifier ->
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    } else @Composable { modifier: Modifier -> Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = subtitle, style = MaterialTheme.typography.labelMedium)
    } }

    if(icon == null) textLabel(modifier)
    else Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null)
        textLabel(Modifier)
    }
}

@Composable
fun KeyDefinitions(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    icon: ImageVector? = null,
    extra: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconLabel(title = title, subtitle = subtitle, icon = icon)
        extra()
    }
}

@Composable
fun IndexLabel(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KeyDefinitions(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        extra = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go"
                )
            }
        }
    )
}

@Composable
fun PersonalInfo(
    updateEnabled: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onCommit: () -> Unit,
    modifier: Modifier = Modifier
) {
    TitledCard(
        title = "Información personal",
        icon = Icons.Default.Person,
        modifier = modifier
    ) {
//        Text(text = "Nombre")
        EnhancedTextField(
            value = name,
            onValueChange = onNameChange,
            title = "Nombre",
            icon = Icons.Default.Person,
            placeholder = "Ingrese un nombre"
        )
//        Text(text = "Correo electrónico")
        EnhancedTextField(
            value = email,
            onValueChange = onEmailChange,
            title = "Correo electrónico",
            icon = Icons.Default.Email,
            placeholder = "Ingrese un email"
        )
//        Text(text = "Contraseña")
        EnhancedTextField(
            value = password,
            onValueChange = onPasswordChange,
            title = "Contraseña",
            icon = Icons.Default.Lock,
            placeholder = "Confirme su contraseña",
            visualTransformation = PasswordVisualTransformation()
        )
        LabeledButton(
            title = "Guardar cambios",
            onClick = onCommit,
            modifier = Modifier.fillMaxWidth(),
            enabled = updateEnabled
        )
    }
}

@Composable
fun SecuritySettings(
    exitEnabled: Boolean,
    onPasswordChangeRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    TitledCard(
        title = "Seguridad",
        icon = Icons.Default.Lock,
        modifier = modifier
    ) {
        IndexLabel(
            title = "Cambiar contraseña",
            subtitle = "Actualiza tu contraseña",
            onClick = if(exitEnabled) { -> onPasswordChangeRequest() } else { ->
                Toast.makeText(
                    context,
                    "You can't do that right now.",
                    Toast.LENGTH_SHORT
                ).show()}
        )
    }
}

@Composable
fun AccountPage(
    updateEnabled: Boolean,
    exitEnabled: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onCommit: () -> Unit,
    onPasswordChangeRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PersonalInfo(
            updateEnabled = updateEnabled,
            name = name,
            onNameChange = onNameChange,
            email = email,
            onEmailChange = onEmailChange,
            password = password,
            onPasswordChange = onPasswordChange,
            onCommit = onCommit
        )
        SecuritySettings(exitEnabled, onPasswordChangeRequest)
    }
}
