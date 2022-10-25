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
import io.yoath.sports.model.AuthTypes.Companion.BASIC_USER
import io.yoath.sports.model.AuthTypes.Companion.COACH_USER

/**
 * Created by ChazzCoin : December 2019.
 */

open class AuthTypes {
    companion object {
        // Auth Types
        var MASTER = "master"
        var ADMIN = "admin"
        var ORG_ADMIN_USER = "org_admin"
        var COACH_USER = "coach"
        var PLAYER_USER = "player"
        var PARENT_USER = "parent"
        var BASIC_USER = "basic" // Default
        var WAITING = "waiting"
    }
}

open class User(uid:String = "", name:String? = "", email:String? = "") : RealmObject() {

    @PrimaryKey
    var uid = "" // SETUP VIA FIREBASE TO LINK TO AUTH SYSTEM
    var name: String? = ""
    var email: String? = ""
    var auth: String = BASIC_USER // "basic"
    var phone: String? = ""
    var organization: String? = ""
    var visibility: String = "closed"

    fun isCoachUser() : Boolean {
        if (this.auth == COACH_USER) return true
        return false
    }
    fun isBasicUser() : Boolean {
        if (this.auth == BASIC_USER) return true
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

