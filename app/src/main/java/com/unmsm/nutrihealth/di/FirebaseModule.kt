package com.unmsm.nutrihealth.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.unmsm.nutrihealth.data.repository.FirebaseAuthManager
import com.unmsm.nutrihealth.data.repository.FirebaseUserRepository
import com.unmsm.nutrihealth.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseFirestore() : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() : FirebaseStorage = FirebaseStorage.getInstance()

    /*
    @Provides
    @Singleton
    fun provideUserRepository(db: FirebaseFirestore = provideFirebaseFirestore()) : UserRepository =
        //FirebaseUserRepository(db)
*/

    @Provides
    @Singleton
    fun provideAuthManager(auth: FirebaseAuth = provideFirebaseAuth()) : FirebaseAuthManager =
        FirebaseAuthManager(auth)
}