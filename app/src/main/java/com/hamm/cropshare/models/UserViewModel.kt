package com.hamm.cropshare.models

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.hamm.cropshare.data.Constants
import com.hamm.cropshare.data.Constants.Companion.ERROR_GETTING_STORE_INFO
import com.hamm.cropshare.data.DataCache
import com.hamm.cropshare.data.Location
import com.hamm.cropshare.data.Store
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.prefs

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

    private var _location = MutableLiveData<Location>()
    val location: LiveData<Location>
        get() = _location

    private var _streetAddress = MutableLiveData<String>()
    val streetAddress: LiveData<String>
        get() = _streetAddress

    private var _zipCode = MutableLiveData<String>()
    val zipCode: LiveData<String>
        get() = _zipCode

    private var _hasError = MutableLiveData<String>()
    val hasError: LiveData<String>
        get() = _hasError

    fun userLogin(email: String, password: String) {
        _isLoading.value = true
        FirebaseHelper().firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _userId.value = it.user?.uid
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

        prefs.userUidPref?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .delete()
        }

        FirebaseHelper().firebaseAuth.currentUser?.let {
            it.delete().addOnSuccessListener {
                _isLoggedIn.value = false
                _isLoading.value = false
            }.addOnFailureListener {exception ->
            Log.d("Failure", exception.toString())
                _isLoading.value = false
            }
        }
    }

    private fun userCreateInDB(userId: String) {
        val userEntry = mapOf(
            "email" to FirebaseHelper().firebaseAuth.currentUser?.email
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
                    userCreateInDB(uid)
                    _storeExists.value = false
                }
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isLoggedIn.value = false
            }
    }

    fun getUserStoreInformation() {
        prefs.userUidPref?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        try {
                            val store = it.result?.get("store") as Map<*, *>
                            val location = store["location"] as Map<*, *>
                            val streetAddress = location["streetAddress"] as String
                            val zipCode = location["zipCode"] as String
                            _location.value = Location(streetAddress, zipCode)
                        } catch (exception: NullPointerException) {
                            Log.d("UserViewModel", "Error retrieving user store info")
                        }
                    }
                }
        }
    }

    fun updateUserStoreLocation(location: Location) {
        val locationEntry = mapOf(
            "streetAddress" to location.streetAddress,
            "zipCode" to location.zipCode
        )
        prefs.userUidPref?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .update("store.location", locationEntry)
                .addOnSuccessListener {
                    _location.value = location
                }
        }
    }

    fun getUserZipCode() {
        FirebaseHelper().firebaseAuth.currentUser?.uid?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .addSnapshotListener { value, _ ->
                    if (value?.get("zipCode") != null) {
                        _zipCode.value = value.get("zipCode") as String
                    }
                }
        }
    }

    fun doesUserStoreExist() {
        FirebaseHelper().firebaseUserUID?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val store = it.result?.get("store") as Map<*, *>
                        val storeName = store["storeName"] as String
                        _storeExists.value = storeName.isNotEmpty()
                    }
                }
        }
    }

    fun userUpdateZipCode(zipCode: String) {
        FirebaseHelper().firebaseAuth.currentUser?.uid?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .update("zipCode", zipCode)
                .addOnSuccessListener {
                    _zipCode.value = zipCode
                }
                .addOnFailureListener {
                    _hasError.value = Constants.ERROR_SETTING_ZIPCODE
                }
        }
    }
}