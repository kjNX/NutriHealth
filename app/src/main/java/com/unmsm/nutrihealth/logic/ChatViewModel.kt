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

    // Esta función es para generar contenido usando la API de Gemini
    fun sendMessage(prompt: String) {
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Agregar el mensaje del usuario a la lista
        _messages.value = _messages.value + Message(content = prompt, time = timestamp, isOwned = true)

        // Llamar a la función suspendida para generar el texto
        viewModelScope.launch {
            val generatedText = generateStoryFromPrompt(prompt)
            _messages.value = _messages.value + Message(content = generatedText, time = timestamp, isOwned = false)
        }
    }

    // Función suspendida que hace la llamada a la API de Gemini
    suspend fun generateStoryFromPrompt(prompt: String): String {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.0-flash")  // Verifica que el modelo sea correcto

        return try {
            val response = model.generateContent(prompt)
            response.text ?: "Sin respuesta."
        } catch (e: Exception) {
            "Error al generar contenido: ${e.message}"
        }
    }
}
