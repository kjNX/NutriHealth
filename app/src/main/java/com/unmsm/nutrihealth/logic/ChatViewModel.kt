package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.unmsm.nutrihealth.data.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // Esta función es para generar contenido usando la API de Gemini, con prompts específicos
    fun sendMessage(expertType: String, userRequest: String) {
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Agregar el mensaje del usuario a la lista
        _messages.value = _messages.value + Message(content = userRequest, time = timestamp, isOwned = true)

        // Llamar a la función suspendida para generar el texto con un prompt específico basado en el tipo de experto
        viewModelScope.launch {
            val generatedText = generateStoryFromPrompt(expertType, userRequest)
            _messages.value = _messages.value + Message(content = generatedText, time = timestamp, isOwned = false)
        }
    }
    // Función suspendida que genera contenido basado en el tipo de experto y la solicitud del usuario
    suspend fun generateStoryFromPrompt(expertType: String, userRequest: String): String {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.0-flash")  // Verifica que el modelo sea correcto

        // Generar un prompt conciso y específico, agregando el límite de 300 palabras
        val prompt = when (expertType) {
            "Nutrición" -> """
            Eres un nutricionista experto. Inicia con un saludo y luego proporciona recomendaciones de dieta para ${userRequest}, pero de manera breve y clara. 
            Limita tu respuesta a 250 palabras, sin exceder este límite.
        """.trimIndent()
            "Entrenamiento Personal" -> """
            Eres un entrenador personal. Inicia con un saludo y luego proporciona un plan de entrenamiento breve y efectivo para ${userRequest}.
            Limita tu respuesta a 250 palabras, sin exceder este límite.
        """.trimIndent()
            "Asesor de Bienestar" -> """
            Eres un asesor de bienestar. Inicia con un saludo y luego da consejos rápidos y prácticos para mejorar el bienestar de alguien que quiere ${userRequest}.
            Limita tu respuesta a 250 palabras, sin exceder este límite.
        """.trimIndent()
            else -> """
            Eres un experto en bienestar. Inicia con un saludo y luego ofrece una respuesta breve y clara para la solicitud de ${userRequest}.
            Limita tu respuesta a 250 palabras, sin exceder este límite.
        """.trimIndent()
        }

        return try {
            val response = model.generateContent(prompt)
            val cleanedResponse = cleanMarkdown(response.text ?: "Sin respuesta.")  // Limpiar texto Markdown
            // Limitar la respuesta a un máximo de 300 palabras
            val wordLimit = 300
            val limitedResponse = cleanedResponse.split(" ").take(wordLimit).joinToString(" ")

            limitedResponse
        } catch (e: Exception) {
            "Error al generar contenido: ${e.message}"
        }
    }

    // Función para limpiar el Markdown (eliminar negritas, cursivas y otros formatos)
    fun cleanMarkdown(text: String): String {
        // Eliminar negritas y cursivas representadas por los asteriscos
        val regex = Regex("\\*\\*(.*?)\\*\\*|\\*(.*?)\\*")
        return text.replace(regex, "$1")  // Reemplaza el texto con asteriscos por solo el contenido limpio
    }
}




