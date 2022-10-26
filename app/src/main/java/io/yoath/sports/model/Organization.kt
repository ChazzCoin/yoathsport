package io.yoath.sports.model

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.RealmList
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.coachUser.manage.LocManageFragment
import io.realm.RealmObject
import io.yoath.sports.ui.BaseListAdapter
import io.yoath.sports.utils.*
import kotlinx.android.synthetic.main.dialog_ask_user_logout.*
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by ChazzCoin : December 2019.
 */
open class Organization : RealmObject(), Serializable {

    @PrimaryKey
    var id: String? = "" //UUID
    var name: String? = "" //Name Given by Manager
    var addressOne: String? = "" // 2323 20th Ave South
    var addressTwo: String? = "" // 2323 20th Ave South
    var city: String? = "" // Birmingham
    var state: String? = "" // AL
    var zip: String? = "" // 35223
    var sport: String? = "unassigned"
    var type: String? = "unassigned"
    var subType: String? = "unassigned"
    var details: String? = ""
    var managerId: String? = "unassigned"
    var managerName: String? = "unassigned"
    var staff: RealmList<String>? = null
    var estPeople: String? = ""


    fun matches(org: Organization) : Boolean {
        if (this.id == org.id &&
            this.name == org.name &&
            this.addressOne == org.addressOne &&
            this.addressTwo == org.addressTwo &&
            this.city == org.city &&
            this.state == org.state &&
            this.zip == org.zip &&
            this.estPeople == org.estPeople) return true
        return false
    }

}

fun View.getTextView(res: Int) : TextView {
    return this.findViewById(res)
}

fun View.getRecyclerView(res: Int) : RecyclerView {
    return this.findViewById(res)
}

fun View.getImageButton(res: Int) : ImageButton {
    return this.findViewById(res)
}

fun View.getButton(res: Int) : Button {
    return this.findViewById(res)
}

fun View.getLinearLayout(res: Int) : LinearLayout {
    return this.findViewById(res)
}

//class OrgViewHolder(itemView: View) : BaseViewHolder(itemView) {
//    /**
//     * itemLocationName
//     * detailsLinearLayout
//     * addressLinearLayout
//     * txtAddressOne
//     * txtAddressTwo
//     * txtCityStateZip
//     * txtPeople
//     * btnAddEditLocationManage
//     * btnMinusLocationManage
//     */
//
//    var txtOrgName = itemView.findViewById<TextView>(R.id.itemLocationName)
//    var txtAddressOne = itemView.getTextView(R.id.txtAddressOne)
//
//    fun bind(org: Organization) {
//        txtOrgName.text = org.name
//        txtAddressOne.text = org.id
//    }
//}

fun RecyclerView.initRealmList(realmList: RealmList<Any>, context: Context) : BaseListAdapter {
    val adapter = BaseListAdapter(realmList)
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    return adapter
}

fun HashMap<*,*>.toOrgRealmList(): RealmList<Any> {
    var resultList: RealmList<Any> = RealmList()
    for ((_,v) in this) {
        val test = (v as? HashMap<*,*>)?.toJSON()
        resultList.add(test)
    }
    return resultList
}

private fun <E> RealmList<E>.add(element: JSONObject?) {
    this.add(element as? E)
}

fun Organization.createDeleteLocationDialog(fragment: LocManageFragment) : Dialog {
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
fun Organization.addUpdateLocationToFirebase(fragment: LocManageFragment? = null) {
    Log.d("Location: ", "addUpdateLocationToFirebase")
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
        .child(AuthController.USER_ID).child(this.id.toString())
        .setValue(this)
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            fragment?.reloadLocAdapter()
            fragment?.let { showSuccess(it.requireContext(), "Location Added!") }
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            fragment?.let { showFailedToast(it.requireContext()) }
        }
}

fun Organization.removeFromFirebase(fragment: LocManageFragment? = null) {
    Log.d("Location: ", "removeFromFirebase")
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.PROFILES).child(FireHelper.LOCATIONS)
        .child(AuthController.USER_ID).child(this.id.toString())
        .removeValue()
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            fragment?.reloadLocAdapter()
            fragment?.let { showSuccess(it.requireContext(), "Location Removed!") }
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            fragment?.let { showFailedToast(it.requireContext()) }
        }
}