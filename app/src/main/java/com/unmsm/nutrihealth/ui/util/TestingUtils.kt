package com.unmsm.nutrihealth.ui.util

import com.unmsm.nutrihealth.data.model.Food
import com.unmsm.nutrihealth.data.model.Run
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun generateSampleFoodData(): List<Food> {
    val foodNames = listOf(
        "Apple", "Banana", "Chicken Breast", "Broccoli", "Rice",
        "Salmon", "Bread", "Eggs", "Milk", "Cheese",
        "Spinach", "Potatoes", "Yogurt", "Orange", "Beef Steak",
        "Carrots", "Pasta", "Tuna", "Avocado", "Oats",
        "Cucumber", "Tomatoes", "Onions", "Pork Chop", "Lettuce",
        "Grapes", "Sweet Potato", "Bell Pepper", "Mushrooms", "Shrimp"
    )

    val foodList = mutableListOf<Food>()
    val fiveDaysAgo = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5))

    for (i in 1..30) {
        val name = foodNames[Random.nextInt(foodNames.size)]
        val energy = Random.nextDouble(50.0, 500.0) // kcals
        val protein = Random.nextDouble(0.0, 30.0) // grams
        val fats = Random.nextDouble(0.0, 25.0) // grams
        val water = Random.nextDouble(10.0, 90.0) // grams

        // Generate a timestamp within the last 5 days
        val randomTimeOffset = Random.nextLong(0, System.currentTimeMillis() - fiveDaysAgo.time)
        val timestamp = Date(fiveDaysAgo.time + randomTimeOffset)

        foodList.add(Food(name, energy, protein, fats, water, timestamp))
    }
    return foodList.toList()
}

fun generateSampleRunData(): List<Run> {
    val runs = mutableListOf<Run>()
    val random = Random.Default
    val calendar = Calendar.getInstance()

    // Generate 30 unique run entries
    for (i in 1..10) {
        calendar.time = Date()
        val daysAgo = random.nextInt(30)
        calendar.add(Calendar.DAY_OF_MONTH, -daysAgo)

        calendar.set(Calendar.HOUR_OF_DAY, random.nextInt(24))
        calendar.set(Calendar.MINUTE, random.nextInt(60))
        calendar.set(Calendar.SECOND, random.nextInt(60))
        calendar.set(Calendar.MILLISECOND, random.nextInt(1000))
        val timestamp = calendar.time

        val durationMinutes = random.nextLong(10, 121)
        val durationInMillis = durationMinutes * 60 * 1000L

        val avgSpeedInKMH = random.nextFloat() * (15.0f - 5.0f) + 5.0f

        val durationInHours = durationInMillis / (1000.0 * 60 * 60)
        val distanceInKM = avgSpeedInKMH * durationInHours
        val distanceInMeters = (distanceInKM * 1000).toInt()

        val caloriesPerKm = random.nextFloat() * (100.0f - 60.0f) + 60.0f
        val caloriesBurned = (distanceInKM * caloriesPerKm).toInt()

        runs.add(
            Run(
                timestamp = timestamp,
                avgSpeedInKMH = avgSpeedInKMH,
                distanceInMeters = distanceInMeters,
                durationInMillis = durationInMillis,
                caloriesBurned = caloriesBurned,
                id = i
            )
        )
    }
    return runs
}
