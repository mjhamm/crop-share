package com.hamm.cropshare.extensions

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.helpers.FirebaseHelper

fun isUpdatedItemValid(storeItem: StoreItem): Boolean {
    storeItem.itemPrice?.let {
        if (storeItem.itemName?.isEmpty() == true || it < 0 || storeItem.itemSellAmount?.type?.isEmpty() == true) {
            return false
        }
        return true
    } ?: run {
        return false
    }
}

fun convertPriceToDouble(updatedPrice: String): Double? {
    return try {
        updatedPrice.toDouble()
    } catch (exception: NumberFormatException) {
        Log.e("convertPriceToDouble", exception.toString())
        null
    }
}

fun Context.createToast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun createSnackbar(view: View, message: CharSequence, length: Int = Snackbar.LENGTH_SHORT) = Snackbar.make(view, message, length).show()

fun isUserLoggedIn(): Boolean {
    if (FirebaseHelper().firebaseAuth.currentUser != null) {
        return true
    }
    return false
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}