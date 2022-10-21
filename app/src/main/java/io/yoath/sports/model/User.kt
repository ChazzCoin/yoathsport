package io.yoath.sports.model

import android.content.Context
import androidx.room.PrimaryKey
import com.google.firebase.database.*
import io.yoath.sports.AuthController
import io.yoath.sports.utils.FireHelper
import io.yoath.sports.utils.showFailedToast
import io.yoath.sports.utils.showSuccess
import io.realm.Realm
import io.realm.RealmObject
/**
 * Created by ChazzCoin : December 2019.
 */
open class User(uid:String = "", name:String? = "", email:String? = "") : RealmObject() {

    companion object {
        var ADMIN = "admin"
        var LOCATION_MANAGER = "location_manager"
        var FOODTRUCK_MANAGER = "foodtruck_manager"
        var WAITING = "waiting"
    }

    @PrimaryKey
    var uid = ""
    var name: String? = null
    var email: String? = null
    var auth: String? = null
    var phone: String? = null

    init {
        this.uid = uid
        this.name = name
        this.email = email
        this.auth = ""
    }
    fun isLocationManager() : Boolean {
        if (this.auth == LOCATION_MANAGER) return true
        return false
    }
    fun isFoodTruckManager() : Boolean {
        if (this.auth == FOODTRUCK_MANAGER) return true
        return false
    }

    fun equals(user: User) : Boolean {
        if (this.uid == user.uid &&
                this.name == user.name &&
                this.auth == user.auth &&
                this.email == user.email &&
                this.phone == user.phone) return true
        return false
    }

}

fun updateUser(newUser: User) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction { r : Realm ->
        // Get a turtle to update.
        val user = r.where(User::class.java).findFirst()
        // Update properties on the instance.
        // This change is saved to the realm.
        user.apply {
            this?.auth = newUser.auth
            this?.name = newUser.name
            this?.email = newUser.email
            this?.phone = newUser.phone
        }
    }
}

//GET CURRENT CART
fun getUser(): User? {
    val realm = Realm.getDefaultInstance()
    var user : User? = User()
    realm?.let {
        user = it.where(User::class.java).findFirst()
    }
    return user
}

fun getProfileUpdatesFirebase(mContext: Context, uid:String) {
    val database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.USERS).child(uid)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temp: User? = dataSnapshot.getValue(User::class.java)
                temp?.let {
                    if (it.uid == uid){
                        AuthController.USER_AUTH = it.auth.toString()
                        AuthController.USER_UID = it.uid
//                        it.getFoodtrucksFromFirebase(mContext)
                        Session.updateUser(it)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                showFailedToast(mContext)
            }
        })
}

fun User.addUpdateToFirebase(mContext: Context) {
    val database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES)
        .child(FireHelper.USERS)
        .child(AuthController.USER_UID)
        .setValue(this)
        .addOnSuccessListener {
            //success
            Session.updateUser(this)
            showSuccess(mContext, "Successfully updated User Info.")
        }.addOnFailureListener {
            //failure
            showFailedToast(mContext, "Failed to update User Info.")
        }
}

