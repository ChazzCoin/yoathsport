package io.usys.report.model

import androidx.room.PrimaryKey
import io.realm.RealmList
import io.realm.RealmObject
import java.io.Serializable

/**
 * Created by ChazzCoin : December 2019.
 */
open class Sport : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var type: String? = ""
    var subType: String? = ""
    var details: String? = ""
    var managerId: String? = ""
    var managerName: String? = ""
    var staff: RealmList<String>? = null

}


////-> SAVE location object to firebase
//fun Sport.addUpdateLocationToFirebase(fragment: LocManageFragment? = null) {
//    Log.d("Location: ", "addUpdateLocationToFirebase")
//    firebase { database ->
//        database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
//            .child(AuthController.USER_UID).child(this.id.toString())
//            .setValue(this)
//            .addOnSuccessListener {
//                //TODO("HANDLE SUCCESS")
//                Session.addLocation(this)
//                fragment?.reloadLocAdapter()
//                fragment?.let { showSuccess(it.requireContext(), "Location Added!") }
//            }.addOnCompleteListener {
//                //TODO("HANDLE COMPLETE")
//            }.addOnFailureListener {
//                //TODO("HANDLE FAILURE")
//                fragment?.let { showFailedToast(it.requireContext()) }
//            }
//    }
//
//}

//fun Sport.removeFromFirebase(fragment: LocManageFragment? = null) {
//    Log.d("Location: ", "removeFromFirebase")
//    val database: DatabaseReference?
//    database = FirebaseDatabase.getInstance().reference
//    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
//        .child(AuthController.USER_UID).child(this.id.toString())
//        .removeValue()
//        .addOnSuccessListener {
//            //TODO("HANDLE SUCCESS")
//            Session.removeLocation(this)
//            fragment?.reloadLocAdapter()
//            fragment?.let { showSuccess(it.requireContext(), "Location Removed!") }
//        }.addOnCompleteListener {
//            //TODO("HANDLE COMPLETE")
//        }.addOnFailureListener {
//            //TODO("HANDLE FAILURE")
//            fragment?.let { showFailedToast(it.requireContext()) }
//        }
//}