package com.hamm.cropshare.helpers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.data.User

class FirebaseHelper {

    val fireStoreDatabase = FirebaseFirestore.getInstance()
    val firebaseAuth = Firebase.auth
    val firebaseUserUID = Firebase.auth.currentUser?.uid

    /**
     * Adds a user to Firestore
     *
     * @param user - the current user
     *
     * We create a unique id and
     */
    fun addUser(user: User) {}

    fun deleteUser(user: User) {}

    fun addStore(user: User) {}

    fun addItemToStore(user: User, storeItem: StoreItem) {}

}