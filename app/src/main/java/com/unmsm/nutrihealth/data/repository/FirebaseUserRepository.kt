package com.unmsm.nutrihealth.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unmsm.nutrihealth.data.model.User

const val TAG = "FIREBASEUSERREPOSITORY"

class FirebaseUserRepository(private val db: FirebaseFirestore) : UserRepository {
    private val userCollection: CollectionReference = db.collection("user")
    private val userDataCollection: CollectionReference = db.collection("user_data")
    private val userTargetCollection: CollectionReference = db.collection("user_target")

    override fun getUser(userId: String) {
        userCollection.document(userId).get().addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val user = task.getResult().data
                User.name = user?.get("name").toString()
                User.email = user?.get("email").toString()
            }
        }
    }

    override fun getUserData(userId: String) {
//        userDataCollection.document(userId).addOnCompleteListener { task ->
//            if(task.isSuccessful) {
//                val user = task.getResult().data
//                User.name = user?.get("name").toString()
//                User.email = user?.get("email").toString()
//            }
//        }
    }

    override fun getUserTarget(userId: String) {
        TODO("Not yet implemented")
    }

    override fun createUser(userId: String) {
        userCollection.document(userId).set(User)
    }

    override fun createUserData(userId: String) {
        TODO("Not yet implemented")
    }

    override fun createUserTarget(userId: String) {
        TODO("Not yet implemented")
    }
}
