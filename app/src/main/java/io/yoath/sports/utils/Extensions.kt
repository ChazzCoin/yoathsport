package io.yoath.sports.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.yoath.sports.R
import io.yoath.sports.model.*
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

/**
 * Created by ChazzCoin : December 2019.
 */

inline fun main(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
        block(this)
    }
}

inline fun ioLaunch(crossinline block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
        block(this)
    }
}

fun io() : CoroutineScope {
    return CoroutineScope(Dispatchers.IO + SupervisorJob())
}

fun realm() : Realm {
    return Realm.getDefaultInstance()
}

fun Any?.isNullOrEmpty() : Boolean {
    if (this == null) return true
    when (this) {
        is String -> { if (this.isEmpty() || this.isBlank()) return true }
        is Collection<*> -> { if (this.isEmpty()) return true }
        is RealmList<*> -> { if (this.isEmpty()) return true }
    }
    return false
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    realm().executeTransaction { block(it) }
}

//LAMBA FUNCTION -> Shortcut for firebase.database calls
inline fun firebase(block: (DatabaseReference) -> Unit) {
    block(FirebaseDatabase.getInstance().reference)
}

inline fun session(block: (Session) -> Unit) {
    Session.session?.let { block(it) }
}

inline fun userOrLogout(activity: Activity? = null, block: (User) -> Unit) {
    Session.user?.let { block(it) } ?: run { activity?.let { Session.restartApplication(it) } }
}

fun sessionAndUser(block: (Session, User) -> Unit) {
    session { itSession ->
        userOrLogout { itUser ->
            block(itSession, itUser)
        }
    }
}

inline fun spots(block: (Session, RealmList<Spot>) -> Unit) {
    Session.session?.spots?.let { block(Session.session!!, it) }
}

inline fun locations(block: (RealmList<Location>) -> Unit) {
    Session.session?.locations?.let { block(it) }
}

inline fun sessionFoodtrucks(block: (RealmList<FoodTruck>) -> Unit) {
    Session.session?.foodtrucks?.let { block(it) }
}

fun getSimpleSpinnerAdapter(context: Context, list:ArrayList<String?>) : ArrayAdapter<String?> {
    return ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
}

fun getSpinnerForFoodTruckType(context: Context) : ArrayAdapter<String?> {
    return ArrayAdapter(context, android.R.layout.simple_list_item_1,
        context.resources.getStringArray(R.array.foodtruck_types))
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun dpToPx(dp: Int, context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
        context.resources.displayMetrics
    ).toInt()

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun GradientDrawable.setCornerRadius(
    topLeft: Float = 0F,
    topRight: Float = 0F,
    bottomRight: Float = 0F,
    bottomLeft: Float = 0F
) {
    cornerRadii = arrayOf(
        topLeft, topLeft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    ).toFloatArray()
}

fun showFailedToast(context: Context, mess: String = "There was an Error.") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

fun showSuccess(context: Context, mess: String = "Success!") {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}