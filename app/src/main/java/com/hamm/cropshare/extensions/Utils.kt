package com.hamm.cropshare.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.helpers.FirebaseHelper
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

fun isUpdatedItemValid(storeItem: StoreItem): Boolean {
    storeItem.itemPrice?.let {
        if (storeItem.itemName?.isEmpty() == true || it > 0 || storeItem.itemQuantityType?.isEmpty() == true) {
            return false
        }
        return true
    } ?: run {
        return false
    }
}

fun String.convertPriceToLong(): Boolean {
    return try {
        this.toLong()
        true
    } catch (_: NumberFormatException) {
        false
    }
}

fun Long.convertToCurrency(): String {
    val dec = BigDecimal(this)
    dec.setScale(2, RoundingMode.HALF_DOWN)
    return String.format(Locale.US, ".2d", dec)
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