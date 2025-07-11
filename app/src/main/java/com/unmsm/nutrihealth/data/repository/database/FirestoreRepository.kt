package com.unmsm.nutrihealth.data.repository.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.unmsm.nutrihealth_app.model.live.Actor
import com.unmsm.nutrihealth_app.model.live.Food
import com.unmsm.nutrihealth_app.model.live.Message
import com.unmsm.nutrihealth_app.model.live.User
import com.unmsm.nutrihealth_app.model.live.history.FoodLog
import com.unmsm.nutrihealth_app.model.live.history.RunLog
import com.unmsm.nutrihealth_app.model.live.stage.UserInitial
import com.unmsm.nutrihealth_app.model.live.stage.UserPlan
import com.unmsm.nutrihealth_app.model.live.stage.UserProgress
import com.unmsm.nutrihealth_app.model.live.stage.UserTarget
import kotlinx.coroutines.tasks.await

const val TAG = "FirestoreRepository"

class FirestoreRepository : DatabaseRepository {
    private val instance = FirebaseFirestore.getInstance()

    val userPath = "user"
    val actorPath = "actor"
    val foodPath = "food"


    val messageSubpath = "message"
    val foodSubpath = "food"
    val runSubpath = "run"

    val stageSubpath = "stage"
    val stageInitialDoc = "initial"
    val stageTargetDoc = "target"
    val stagePlanDoc = "plan"
    val stageProgressDoc = "progress"

    override suspend fun getFoodHistory(uid: String): List<FoodLog>? =
        getCollection("$userPath/$uid/$foodSubpath")

    override suspend fun addFoodLog(uid: String, foodLog: FoodLog)
    { addToCollection("$userPath/$uid/$foodSubpath", foodLog) }

    override suspend fun getRunHistory(uid: String): List<RunLog>? =
        getCollection("$userPath/$uid/$runSubpath")

    override suspend fun addRunLog(uid: String, runLog: RunLog)
    { addToCollection("$userPath/$uid/$runSubpath", runLog) }

    override suspend fun getUser(uid: String): User? = getDocument("$userPath/$uid")

    override suspend fun setUser(uid: String, user: User) {
        instance.document("$userPath/$uid").set(user).await()
    }

    override suspend fun getUserInitial(uid: String): UserInitial? =
        getDocument("$userPath/$uid/$stageSubpath/$stageInitialDoc")

    override suspend fun setUserInitial(uid: String, userInitial: UserInitial)
    { setDocument("$userPath/$uid/$stageSubpath/$stageInitialDoc", userInitial) }

    override suspend fun getUserPlan(uid: String): UserPlan? =
        getDocument("$userPath/$uid/$stageSubpath/$stagePlanDoc")

    override suspend fun setUserPlan(uid: String, userPlan: UserPlan)
    { setDocument("$userPath/$uid/$stageSubpath/$stagePlanDoc", userPlan) }

    override suspend fun getUserTarget(uid: String): UserTarget? =
        getDocument("$userPath/$uid/$stageSubpath/$stageTargetDoc")

    override suspend fun setUserTarget(uid: String, userTarget: UserTarget)
    { setDocument("$userPath/$uid/$stageSubpath/$stageTargetDoc", userTarget) }

    override suspend fun getUserProgress(uid: String): UserProgress? =
        getDocument("$userPath/$uid/$stageSubpath/$stageProgressDoc")

    override suspend fun setUserProgress(uid: String, userProgress: UserProgress)
    { setDocument("$userPath/$uid/$stageSubpath/$stageProgressDoc", userProgress) }

    override suspend fun getActors(): List<Actor>? =
        getCollection(actorPath)

    override suspend fun getFood(category: String): Food? =
        getDocument("$foodPath/$category")

    override suspend fun getMessages(
        uid: String,
        actorID: Int
    ): List<Message>? {
        return emptyList()
    }

    override suspend fun addMessage(
        uid: String,
        actorID: Int,
        message: Message
    ) {
        /*
        try {
            instance.document("$userPath/$uid/$messageSubpath/$")
        } catch (e: Exception) {
            Log.d(TAG, "addMessage: ")
        }
         */
    }

    // Actual methods used
    private suspend inline fun<reified T> getDocument(path: String) : T? = try {
        val document = instance.document(path)
            .get().await()
        document.toObject()
    } catch (e: FirebaseFirestoreException) {
        Log.d(TAG, "getDocument: Failed to fetch data from Firestore. ${e.message}")
        null
    } catch (e: Exception) {
        Log.d(TAG, "getDocument: An error has ocurred. ${e.message}")
        null
    }

    private suspend inline fun<reified T> getCollection(path: String) : List<T>? = try {
        val collection = instance.collection(path).get().await()
        val list = mutableListOf<T>()
        for(document in collection.documents) {
            val data = document.toObject<T>()
            if(data != null) list.add(data)
        }
        list.toList()
    } catch (e: FirebaseFirestoreException) {
        Log.d(TAG, "getCollection: Failed to fetch data from Firestore. ${e.message}")
        null
    } catch (e: Exception) {
        Log.d(TAG, "getCollection: An error has ocurred. ${e.message}")
        null
    }

    private suspend fun<T: Any> setDocument(path: String, obj: T) =
        instance.document(path).set(obj).await()

    private suspend fun<T: Any> addToCollection(path: String, obj: T) =
        instance.collection(path).add(obj).await()
}