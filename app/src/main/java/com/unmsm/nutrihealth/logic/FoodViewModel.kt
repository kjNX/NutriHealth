package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import java.util.Date

class FoodViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _foodList = MutableStateFlow<List<Food>>(emptyList())
    val foodList: StateFlow<List<Food>> = _foodList

    private fun foodCollection(): CollectionReference? {
        val userId = User.id
        Log.d("FoodViewModel", "User.id: '$userId'")
        return if (userId.isNotEmpty()) {
            db.collection("users").document(userId).collection("foods")
        } else null
    }

    private fun predictedFoodCollection(): CollectionReference? {
        val userId = User.id
        Log.d("FoodViewModel", "User.id: '$userId'")
        return if (userId.isNotEmpty()) {
            db.collection("users").document(userId).collection("foodsPr")
        } else null
    }

    fun loadFood() {
        val collection = foodCollection()
        if (collection == null) {
            Log.w("FoodViewModel", "No se cargó comida: User.id está vacío.")
            return
        }

        Log.d("FoodViewModel", "Cargando comidas desde Firestore...")

        collection.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Food::class.java) }
                _foodList.value = list
                Log.d("FoodViewModel", "Se cargaron ${list.size} comidas.")
            }
            .addOnFailureListener { exception ->
                Log.e("FoodViewModel", "Error al cargar comidas: ${exception.message}", exception)
            }
    }

    fun addFood(food: Food, onResult: (Boolean) -> Unit) {
        val collection = foodCollection()
        if (collection == null) {
            onResult(false)
            return
        }

        collection.add(food)
            .addOnSuccessListener {
                loadFood()
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun savePredictedFood(food: Food, onResult: (Boolean, String) -> Unit) {
        val collection = predictedFoodCollection()
        if (collection == null) {
            onResult(false, "Error: Usuario no identificado")
            return
        }

        // Crear un mapa con los datos de la comida
        val foodData = mapOf(
            "name" to food.name,
            "energy" to food.energy,
            "protein" to food.protein,
            "fat" to food.fat,
            "water" to food.water,
            "timestamp" to Date()
        )

        collection.add(foodData)
            .addOnSuccessListener {
                Log.d("FoodViewModel", "Comida predicha guardada con éxito")
                onResult(true, "Comida guardada exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("FoodViewModel", "Error al guardar comida predicha: ${e.message}")
                onResult(false, "Error al guardar: ${e.message}")
            }
    }
}
