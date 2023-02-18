package com.hamm.cropshare.models

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.hamm.cropshare.data.Constants
import com.hamm.cropshare.helpers.FirebaseHelper

class UserViewModel: ViewModel() {

    private var _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private var _storeExists = MutableLiveData<Boolean>()
    val storeExists: LiveData<Boolean>
        get() = _storeExists

    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _zipCodeChange = MutableLiveData<Long>()
    val zipCodeChange: LiveData<Long>
        get() = _zipCodeChange

    private var _hasError = MutableLiveData<String>()
    val hasError: LiveData<String>
        get() = _hasError

    fun userLogin(email: String, password: String) {
        _isLoading.value = true
        FirebaseHelper().firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isLoggedIn.value = true
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isLoggedIn.value = false
            }
    }

    fun userLogout() {
        _isLoading.value = true
        FirebaseHelper().firebaseAuth.signOut()
        _isLoggedIn.value = false
        // Add in artificial delay to show loading screen when logout is pressed
        // This makes the user feel like something actually happened
        Handler(Looper.getMainLooper()).postDelayed({
            _isLoading.value = false
        }, 500)
    }

    fun userDeleteAccount() {
        val uid = FirebaseHelper().firebaseAuth.currentUser?.uid
        _isLoading.value = true

        FirebaseHelper().firebaseAuth.currentUser?.let {
            it.delete().addOnSuccessListener {
                _isLoggedIn.value = false
                _isLoading.value = false
            }
        }

        uid?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .delete()
        }
    }

    fun userCreateInDB(userId: String) {
        val userEntry = mapOf(
            "Email" to FirebaseHelper().firebaseAuth.currentUser?.email
        )
        FirebaseHelper().fireStoreDatabase.collection("users").document(userId)
            .set(userEntry)
    }

    fun userRegister(email: String, password: String) {
        _isLoading.value = true
        FirebaseHelper().firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {authResult ->
                _isLoggedIn.value = true
                authResult.user?.uid?.let { uid ->
                    _userId.value = uid
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isLoggedIn.value = false
            }
    }

    fun getUserZipCode() {
        FirebaseHelper().firebaseAuth.currentUser?.uid?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .addSnapshotListener { value, _ ->
                    if (value?.get("zipCode") != null) {
                        _zipCodeChange.value = value.get("zipCode") as Long
                    }
                }
        }
    }

    fun doesUserStoreExist() {
        FirebaseHelper().firebaseUserUID?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .addSnapshotListener { value, _ ->
                    val store = value?.get("store")
                    _storeExists.value = store != null
                }
        }
    }

    fun userUpdateZipCode(zipCode: Long) {
        val zipCodeEntry = mapOf(
            "zipCode" to zipCode
        )
        FirebaseHelper().firebaseAuth.currentUser?.uid?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .set(zipCodeEntry)
                .addOnSuccessListener {
                    _zipCodeChange.value = zipCode
                }
                .addOnFailureListener {
                    _hasError.value = Constants.ERROR_SETTING_ZIPCODE
                }
        }
    }
}