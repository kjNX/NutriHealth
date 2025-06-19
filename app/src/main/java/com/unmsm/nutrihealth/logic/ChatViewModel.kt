package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val content: String,
    val timestamp: String,
    val isUser: Boolean
)

class ChatViewModel : ViewModel() {
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.0-flash")

    // Usamos MutableStateFlow para mantener la lista de mensajes de tipo Message
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun sendMessage(prompt: String) {
        val timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        // Primero, agregamos el mensaje del usuario
        _messages.value += Message(prompt, timestamp, isUser = true)

        // Luego, enviamos la solicitud al modelo Gemini
        viewModelScope.launch {
            try {
                val response = model.generateContent(prompt)
                // Agregamos la respuesta del modelo
                _messages.value += Message(response.text ?: "Sin respuesta.", timestamp, isUser = false)
            } catch (e: Exception) {
                // En caso de error, agregamos un mensaje de error
                _messages.value += Message("Error: ${e.message}", timestamp, isUser = false)
            }
        }
    }
}
