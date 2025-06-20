package com.unmsm.nutrihealth.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.model.UserTarget
import com.unmsm.nutrihealth.ui.theme.NutriHealthTheme

data class ScreenData(
    val title: String,
    val description: String,
    val data: @Composable () -> Unit
)

@Composable
fun ScreenTracker(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Paso $current de $max")
        Row(modifier = Modifier.height(16.dp)) {
            repeat(max - 1) {
                Column(modifier = Modifier
                    .weight(1f)
                    .background(color = if (it <= current) Color.Green else Color.Gray))
                {}
                Spacer(Modifier.width(4.dp))
            }

            Column(modifier = Modifier
                .weight(1f)
                .background(color = if (current == max - 1) Color.Green else Color.Gray))
            {}
        }
    }
}

@Composable
fun ButtonSelector(
    selectedIndex: Int,
    options: List<String>,
    modifier: Modifier = Modifier,
    onTap: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onTap(index) },
                selected = index == selectedIndex,
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    composable: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(text = label)
        composable()
    }
}

@Composable
fun RadioGroup(
    selected: UserTarget.Priority,
    options: List<UserTarget.Priority> = UserTarget.Priority.entries,
    onTap: (UserTarget.Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        options.forEach { i ->
            Row(
                Modifier.fillMaxWidth().height(56.dp).selectable(
                    selected = i == selected,
                    onClick = { onTap(i) },
                    role = Role.RadioButton
                )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = i == selected,
                    onClick = null
                )
                Text(
                    text = selected.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun EssentialData(modifier: Modifier = Modifier) {
    var genderIndex by remember { mutableIntStateOf(0) }
    val genderOptions = listOf("Hombre", "Mujer")
    var intensityIndex by remember { mutableIntStateOf(0) }
    var intensityOptions = listOf("Sedentario", "Ligero", "Moderado", "Intenso")
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    Column {
        FormField(label = "Género") {
            ButtonSelector(genderIndex, genderOptions) { genderIndex = it }
        }
        FormField(label = "Edad") {
            TextField(
                value = age,
                onValueChange = {
                    if (age == "" || age.toIntOrNull() != null) age = it
                },
                placeholder = { Text("Años") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        FormField(label = "Altura") {
            TextField(
                value = height,
                onValueChange = {
                    if (height == "" || height.toIntOrNull() != null) height = it
                },
                placeholder = { Text("cm") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        FormField(label = "Peso actual") {
            TextField(
                value = weight,
                onValueChange = {
                    if (weight == "" || weight.toFloatOrNull() != null) weight = it
                },
                placeholder = { Text("kg") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        FormField(label = "Nivel de actividad") {
            ButtonSelector(intensityIndex, intensityOptions) { intensityIndex = it }
        }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text(text = "Continuar") }
    }
}

@Composable
fun TargetData(modifier: Modifier = Modifier) {

}

@Composable
fun Confirmation(modifier: Modifier = Modifier) {

}

val setupList = listOf(
    ScreenData(
        "Datos personales",
        "Información básica para calcular tus necesidades",
        {

        }
    ),
    ScreenData(
        "Detalles de objetivo",
        "Define tu meta específica",
        {
        }
    ),
    ScreenData(
        "Resumen y análisis",
        "Basado en tus datos, hemos calculado lo siguiente",
        {

        }
    )
)

@Composable
fun ProgressTopBar(modifier: Modifier = Modifier) {

}

@Composable
fun InitialForm(modifier: Modifier = Modifier) {
    
}

@Composable
fun TargetForm(modifier: Modifier = Modifier) {

}

@Composable
fun Overview(modifier: Modifier = Modifier) {

}

@Composable
fun AccountSetupDisplay(modifier: Modifier = Modifier) {
    
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    NutriHealthTheme {
        ScreenTracker(0, 5)
    }
}