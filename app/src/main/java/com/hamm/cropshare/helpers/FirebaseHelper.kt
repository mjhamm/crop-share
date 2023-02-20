package com.hamm.cropshare.helpers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FirebaseHelper {

    val fireStoreDatabase = FirebaseFirestore.getInstance()
    val firebaseAuth = Firebase.auth
    val firebaseUserUID = Firebase.auth.currentUser?.uid

}