package com.unmsm.nutrihealth.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sdevprem.runtrack.data.tracking.timer.DefaultTimeTracker
import com.unmsm.nutrihealth.logic.service.DefaultBackgroundTrackingManager
import com.unmsm.nutrihealth.data.tracking.location.DefaultLocationTrackingManager
import com.unmsm.nutrihealth.data.model.LocationTrackingManager
import com.unmsm.nutrihealth.logic.background.BackgroundTrackingManager
import com.unmsm.nutrihealth.logic.timer.TimeTracker

import com.unmsm.nutrihealth.data.tracking.location.LocationUtils
import com.unmsm.nutrihealth.data.repository.FoodPredictionService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.MultipartBody
import retrofit2.Response
import com.unmsm.nutrihealth.data.model.FoodPrediction
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.unmsm.nutrihealth.data.model.AIFoodPrediction
import com.unmsm.nutrihealth.data.model.LabelFoodPrediction
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        private const val USER_PREFERENCES_FILE_NAME = "user_preferences"

        @Singleton
        @Provides
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        @Provides
        @Singleton
        fun providesPreferenceDataStore(
            @ApplicationContext context: Context,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
                ),
                produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) },
                scope = scope.plus(ioDispatcher + SupervisorJob())
            )

        @Singleton
        @Provides
        fun provideLocationTrackingManager(
            @ApplicationContext context: Context,
            fusedLocationProviderClient: FusedLocationProviderClient
        ): LocationTrackingManager = DefaultLocationTrackingManager(
            fusedLocationProviderClient = fusedLocationProviderClient,
            context = context,
            locationRequest = LocationUtils.locationRequestBuilder.build()
        )
    }

    @Binds
    @Singleton
    abstract fun provideBackgroundTrackingManager(
        trackingServiceManager: DefaultBackgroundTrackingManager
    ): BackgroundTrackingManager

    @Binds
    @Singleton
    abstract fun provideTimeTracker(
        timeTracker: DefaultTimeTracker
    ): TimeTracker
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @RegularRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FoodPredictionService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @AIRetrofit
    @Provides
    @Singleton
    fun provideAIRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FoodPredictionService.AI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @LabelRetrofit
    @Provides
    @Singleton
    fun provideLabelRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FoodPredictionService.LABEL_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface RegularFoodService {
        @Multipart
        @POST("predict")
        suspend fun predictFood(
            @Part image: MultipartBody.Part
        ): Response<FoodPrediction>
    }

    interface AIFoodService {
        @Multipart
        @POST("estimate/dish")
        suspend fun predictFoodWithAI(
            @Part photo: MultipartBody.Part
        ): Response<AIFoodPrediction>
    }

    interface LabelFoodService {
        @Multipart
        @POST("extract/label")
        suspend fun extractLabel(
            @Part photo: MultipartBody.Part
        ): Response<LabelFoodPrediction>
    }

    @Provides
    @Singleton
    fun provideFoodPredictionService(
        @RegularRetrofit retrofit: Retrofit,
        @AIRetrofit aiRetrofit: Retrofit,
        @LabelRetrofit labelRetrofit: Retrofit
    ): FoodPredictionService {
        val regularService = retrofit.create(RegularFoodService::class.java)
        val aiService = aiRetrofit.create(AIFoodService::class.java)
        val labelService = labelRetrofit.create(LabelFoodService::class.java)

        return object : FoodPredictionService {
            override suspend fun predictFood(image: MultipartBody.Part): Response<FoodPrediction> {
                return regularService.predictFood(image)
            }

            override suspend fun predictFoodWithAI(photo: MultipartBody.Part): Response<AIFoodPrediction> {
                return aiService.predictFoodWithAI(photo)
            }

            override suspend fun extractLabel(photo: MultipartBody.Part): Response<LabelFoodPrediction> {
                return labelService.extractLabel(photo)
            }
        }
    }
}