package com.unmsm.nutrihealth.data.repository

import com.unmsm.nutrihealth.data.model.FoodPrediction
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

    companion object {
        const val BASE_URL = "https://server-comida-520374155933.us-central1.run.app/"
    }
} 