package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.FoodPrediction
import com.unmsm.nutrihealth.data.model.AIFoodPrediction
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FoodPredictionService {
    @Multipart
    @POST("predict")
    suspend fun predictFood(
        @Part image: MultipartBody.Part
    ): Response<FoodPrediction>

    @Multipart
    @POST("estimate/dish")
    suspend fun predictFoodWithAI(
        @Part photo: MultipartBody.Part
    ): Response<AIFoodPrediction>

    companion object {
        const val BASE_URL = "https://server-comida-520374155933.us-central1.run.app/"
        const val AI_BASE_URL = "https://studio-git-master-danielleonardo23s-projects.vercel.app/api/"
    }
} 