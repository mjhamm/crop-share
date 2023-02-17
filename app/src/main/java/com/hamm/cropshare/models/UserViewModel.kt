package com.hamm.cropshare.models

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamm.cropshare.helpers.FirebaseHelper

class UserViewModel: ViewModel() {

    private var _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private var _userId = MutableLiveData<String>()
    val userId: LiveData<String>
        get() = _userId

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

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
        _isLoading.value = true

        FirebaseHelper().firebaseAuth.currentUser?.delete()
            ?.addOnSuccessListener {
                _isLoggedIn.value = false
                _isLoading.value = false
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
}