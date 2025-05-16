package com.unmsm.nutrihealth.logic

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import android.util.Log

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
}
