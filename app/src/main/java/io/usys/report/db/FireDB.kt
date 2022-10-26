package io.usys.report.db

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.usys.report.utils.firebase
import io.usys.report.utils.ioLaunch
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : December 2019.
 */

class FireDB {

    companion object {
        const val SPOT_MONTH_DB = "MMMyyyy"
        const val SPOT_DATE_FORMAT = "yyyy-MM-d"
        const val DATE_MONTH = "MMMM"
        const val FIRE_DATE_FORMAT = "EEE, MMM d yyyy, hh:mm:ss a"
        //ADMIN
        const val ADMIN: String = "admin"
        const val SYSTEM: String = "system"
        // -> Main Database Structure
        const val USERS: String = "users"
        const val ORGANIZATIONS: String = "organizations"
        const val REVIEWS: String = "reviews"

        /**
         * organizations - organization(id)
         * users - user(id)
         * reviews - review(id)
         *
         */
    }
}

//// Verified
//inline fun firebase(block: (DatabaseReference) -> Unit) {
//    block(FirebaseDatabase.getInstance().reference)
//}
//
//// Untested
//fun <T> DataSnapshot.toClass(clazz: Class<T>): T? {
//    return this.getValue(clazz)
//}

fun getFirebaseUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

//works but the timing is off.
suspend fun getAllDB(collection: String) : DataSnapshot? {
    var result: DataSnapshot? = null
    ioLaunch {
        firebase {
            it.child(collection)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        result = dataSnapshot
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }
    return result
}

//untested
fun getDB(collection: String, id: String) : DataSnapshot? {
    var result: DataSnapshot? = null
    firebase {
        it.child(collection).child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    result = dataSnapshot
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
    return result

}

// Verified
fun <T> T.addUpdateInFirebase(collection: String, id: String): Boolean {
    var result = false
    firebase { database ->
        database.child(collection).child(id)
            .setValue(this)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}

// Verified
fun addUpdateDB(collection: String, id: String, obj: Any): Boolean {
    var result = false
    firebase { database ->
        database.child(collection).child(id)
            .setValue(obj)
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                result = true
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                result = false
            }
    }
    return result
}