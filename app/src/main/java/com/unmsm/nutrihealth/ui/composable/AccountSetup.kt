package com.unmsm.nutrihealth.ui.composable

import android.renderscript.ScriptGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unmsm.nutrihealth.data.model.UserTarget
import com.unmsm.nutrihealth.logic.AccountSetupUiState
import com.unmsm.nutrihealth.logic.AccountSetupViewModel
import com.unmsm.nutrihealth.ui.composable.pages.profile.ValueCard
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
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Paso ${current + 1} de $max")
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.height(5.dp)) {
            repeat(max - 1) {
                Column(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(color = if (it <= current) Color.Green else Color.Gray))
                {}
                Spacer(Modifier.width(4.dp))
            }

            Column(modifier = Modifier
                .weight(1f)
                .fillMaxSize()
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
fun RadioGroup(
    selected: UserTarget.Priority = UserTarget.Priority.Health,
    options: List<UserTarget.Priority> = UserTarget.Priority.entries,
    onTap: (UserTarget.Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        options.forEach { i ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = i == selected,
                        onClick = { onTap(selected) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = i == selected,
                    onClick = null
                )
                Text(
                    text = i.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
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
fun ButtonField(
    label: String,
    index: Int,
    options: List<String>,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FormField(label = label, modifier = modifier) {
        ButtonSelector(index, options, onTap = onTap)
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    FormField(label = label, modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SliderField(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Int,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    FormField(label = label, modifier = modifier) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = step
        )
    }
}

@Composable
fun EssentialData(
    genderIndex: Int,
    genderOptions: List<String>,
    intensity: Float,
    age: String,
    height: String,
    weight: String,
    onGenderChange: (Int) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onIntensityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var genderIndex by remember { mutableIntStateOf(0) }
    val genderOptions = listOf("Hombre", "Mujer")
    Column(modifier = modifier) {
        ButtonField("Género", genderIndex, genderOptions, onGenderChange)
        InputField("Edad actual", age, onAgeChange, "años")
        InputField("Altura actual", height, onHeightChange, "cm")
        InputField("Peso actual", weight, onWeightChange, "kg")
        SliderField("Nivel de actividad", intensity, 0f..8f, 7, onIntensityChange)
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text(text = "Continuar") }
    }
}

@Composable
fun TargetData(
    targetWeight: String,
    onWeightChange: (String) -> Unit,
    onGoalChange: (UserTarget.Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FormField(label = "Peso objetivo") {
            TextField(
                value = targetWeight,
                onValueChange = onWeightChange,
                placeholder = { Text("kg") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        InputField("Peso operativo", targetWeight, onWeightChange, "kg")
        FormField(label = "Meta principal") {
            RadioGroup(onTap = onGoalChange)
        }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text(text = "Continuar") }
    }
}

@Composable
fun Confirmation(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        FormField(label = "Metabolismo basal") {
            Text(text = "1,720 kcal", style = MaterialTheme.typography.headlineMedium)
        }
        FormField(label = "Cantidad de calorías recomendada") {
            Text(text = "2,150 kcal", style = MaterialTheme.typography.headlineMedium)
        }
        FormField(label = "Distribución de macronutrientes") {
            Row {
                ValueCard(
                    title = "Proteínas",
                    percentage = 30,
                    amount = 161,
                    color = Color(0xFF9CCC65), // verde claro
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                ValueCard(
                    title = "Carbohidratos",
                    percentage = 45,
                    amount = 242,
                    color = Color(0xFFFFF176), // amarillo suave
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                ValueCard(
                    title = "Grasas",
                    percentage = 25,
                    amount = 60,
                    color = Color(0xFFE57373), // rojo rosado
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    FormField(label = "Tiempo estimado para alcanzar su objetivo") {
        Text(text = "12 semanas", style = MaterialTheme.typography.headlineMedium)
    }
    Text(text = "Al hacer clic en \"Comenzar a utilizar NutriHealth\", usted acepta los Términos y Condiciones del Servicio.")
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
fun FullDisplay(screenData: ScreenData, modifier: Modifier = Modifier) {
    Column {
        Text(text = screenData.title, style = MaterialTheme.typography.titleLarge)
        Text(text = screenData.description)
        screenData.data()
    }
}

@Composable
fun AccountSetupDisplay(
    modifier: Modifier = Modifier,
    viewModel: AccountSetupViewModel = viewModel()
) {
    var pagerState = rememberPagerState(pageCount = { setupList.size })
    var uiState = viewModel.uiState.collectAsState()

    Column {
        ScreenTracker(pagerState.currentPage, setupList.size)
        HorizontalPager(state = pagerState) { i ->
            when(i) {
                0 -> {}
                1 -> {}
                2 -> {}
                else -> {}
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    NutriHealthTheme {
        AccountSetupDisplay()
    }
}