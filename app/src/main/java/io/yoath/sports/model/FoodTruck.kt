package io.yoath.sports.model

import android.content.Context
import androidx.room.PrimaryKey
import com.google.firebase.database.*
import io.yoath.sports.AuthController
import io.yoath.sports.ui.DashboardFragment
import io.yoath.sports.utils.FireHelper
import io.yoath.sports.utils.showFailedToast
import io.yoath.sports.utils.showSuccess
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by ChazzCoin : December 2019.
 */
open class FoodTruck(id:String? = "", userId:String? = "", truckName:String? = "",
                     truckType:String? = "", truckSubType:String? = "", truckFoodType:String? = "") : RealmObject() {

    companion object {
        const val ENTREE = "entree"
        const val DESSERT = "dessert"
    }

    @PrimaryKey
    var id: String? = "" //System.getcurrentmilli
    var userId : String? = "" //ID given to user from firebase
    var truckName: String? = "" //Name Given by Manager
    var truckType: String? = ""
    var truckSubType: String? = ""
    var truckFoodType: String? = ""

    init {
        this.id = id
        this.userId = userId
        this.truckName = truckName
        this.truckType = truckType
        this.truckSubType = truckSubType
        this.truckFoodType = truckFoodType
    }
}

fun getFoodtruck() : FoodTruck? {
    val trucks : RealmList<FoodTruck>? = Session.session?.foodtrucks
    if (!trucks.isNullOrEmpty()) {
        return trucks[0] as FoodTruck
    }
    return null
}

fun createFoodtruck() {
    val realm = Realm.getDefaultInstance()
    val foodtruck = FoodTruck()
    realm?.let { itRealm ->
        itRealm.beginTransaction()
        itRealm.createObject(FoodTruck::class.java)
        itRealm.insert(foodtruck)
        itRealm.commitTransaction()
    }
}

fun User.getFoodtrucksFromFirebase(mContext: Context, dash: DashboardFragment) {
    if (this.isLocationManager()) return
    Session.removeAllTrucks()
    val database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES)
        .child(FireHelper.FOODTRUCKS)
        .child(AuthController.USER_UID)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val foodtrucks: FoodTruck? = ds.getValue(FoodTruck::class.java)
                    foodtrucks?.let {
                        Session.addFoodtruck(it)
                    }
                }
                dash.verifyFoodtruck()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                showFailedToast(mContext)
            }
        })
}

fun FoodTruck.addUpdateForFirebase(mContext: Context) {
    if (this.id.isNullOrEmpty()) return
    val database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES)
        .child(FireHelper.FOODTRUCKS)
        .child(AuthController.USER_UID)
        .child(this.id!!)
        .setValue(this).addOnSuccessListener {
            //success
            Session.removeAllTrucks()
            Session.addFoodtruck(this)
            showSuccess(mContext, "Successfully added Foodtruck.")
        }.addOnFailureListener {
            //failure
            showFailedToast(mContext, "Failed to add Foodtruck.")
        }
}


