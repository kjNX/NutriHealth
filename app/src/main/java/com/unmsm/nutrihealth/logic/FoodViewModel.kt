package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.User
import com.unmsm.nutrihealth.data.model.LabelFoodPrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import java.util.Date

class FoodViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _foodList = MutableStateFlow<List<Food>>(emptyList())
    val foodList: StateFlow<List<Food>> = _foodList
    private val _foodSuggestions = MutableStateFlow<List<Food>>(emptyList())  // base global de alimentos
    val foodSuggestions: StateFlow<List<Food>> = _foodSuggestions
    private fun foodCollection(): CollectionReference? {
        val userId = User.id
        Log.d("FoodViewModel", "User.id: '$userId'")
        return if (userId.isNotEmpty()) {
            db.collection("user").document(userId).collection("foods")
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

        collection.add(food.toMap())
            .addOnSuccessListener {
                loadFood()
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun savePredictedFood(food: Food, onResult: (Boolean, String) -> Unit) {
        val collection = foodCollection()
        if (collection == null) {
            onResult(false, "Error: Usuario no identificado")
            return
        }

        collection.add(food.toMap())
            .addOnSuccessListener {
                Log.d("FoodViewModel", "Comida guardada con éxito")
                loadFood() // Actualizar la lista después de guardar
                onResult(true, "Comida guardada exitosamente")
            }
            .addOnFailureListener { e ->
                Log.e("FoodViewModel", "Error al guardar comida: ${e.message}")
                onResult(false, "Error al guardar: ${e.message}")
            }
    }
    fun loadGlobalFoodSuggestions() {
        db.collection("food").get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Food::class.java) }
                _foodSuggestions.value = list
                Log.d("FoodViewModel", "Se cargaron ${list.size} sugerencias de alimentos.")
            }
            .addOnFailureListener { exception ->
                Log.e("FoodViewModel", "Error al cargar sugerencias: ${exception.message}", exception)
            }
    }

    fun saveLabelPrediction(labelPrediction: LabelFoodPrediction, onResult: (Boolean, String) -> Unit) {
        val food = Food(
            name = labelPrediction.name,
            energy = labelPrediction.energy,
            protein = labelPrediction.protein,
            fats = labelPrediction.fats,
            water = labelPrediction.water,
            timestamp = Date()
        )
        savePredictedFood(food, onResult)
    }
}
