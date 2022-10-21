package io.yoath.sports.model

import android.app.Dialog
import android.util.Log
import androidx.room.PrimaryKey
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.locationManager.manage.LocManageFragment
import io.yoath.sports.utils.FireHelper
import io.yoath.sports.utils.showFailedToast
import io.yoath.sports.utils.showSuccess
import io.realm.RealmObject
import kotlinx.android.synthetic.main.dialog_ask_user_logout.*
import java.io.Serializable

/**
 * Created by ChazzCoin : December 2019.
 */
open class Location(id:String? = "", locationName:String? = "",
                    addressOne:String? = "", addressTwo:String? = "",
                    city:String? = "", state:String? = "", zip:String? = "",
                    estPeople:String? = "") : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var locationName: String? = "" //Name Given by Manager
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var typeOfPlace: String? = ""
    var estPeople: String? = ""
    var details: String? = ""

    init {
        this.id = id
        this.locationName = locationName
        this.addressOne = addressOne
        this.addressTwo = addressTwo
        this.city = city
        this.state = state
        this.zip = zip
        this.estPeople = estPeople
    }

    fun matches(loc: Location) : Boolean {
        if (this.id == loc.id &&
            this.locationName == loc.locationName &&
            this.addressOne == loc.addressOne &&
            this.addressTwo == loc.addressTwo &&
            this.city == loc.city &&
            this.state == loc.state &&
            this.zip == loc.zip &&
            this.estPeople == loc.estPeople) return true
        return false
    }

}

fun Location.createDeleteLocationDialog(fragment: LocManageFragment) : Dialog {
    val dialog = Dialog(fragment.requireActivity())
    dialog.setContentView(R.layout.dialog_ask_user_logout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialog.txtAskUserTitle.text = "Delete?"
    dialog.txtAskUserBody.text = "Are you sure you want to delete this location?"

    // On Clicks
    dialog.btnYesAskUser.setOnClickListener {
        //REMOVE FROM FIREBASE
        this.removeFromFirebase(fragment)
        dialog.dismiss()
    }
    dialog.btnCancelAskUser.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

//-> SAVE location object to firebase
fun Location.addUpdateLocationToFirebase(fragment: LocManageFragment? = null) {
    Log.d("Location: ", "addUpdateLocationToFirebase")
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
        .child(AuthController.USER_UID).child(this.id.toString())
        .setValue(this)
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            Session.addLocation(this)
            fragment?.reloadLocAdapter()
            fragment?.let { showSuccess(it.requireContext(), "Location Added!") }
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            fragment?.let { showFailedToast(it.requireContext()) }
        }
}

fun Location.removeFromFirebase(fragment: LocManageFragment? = null) {
    Log.d("Location: ", "removeFromFirebase")
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
        .child(AuthController.USER_UID).child(this.id.toString())
        .removeValue()
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            Session.removeLocation(this)
            fragment?.reloadLocAdapter()
            fragment?.let { showSuccess(it.requireContext(), "Location Removed!") }
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            fragment?.let { showFailedToast(it.requireContext()) }
        }
}