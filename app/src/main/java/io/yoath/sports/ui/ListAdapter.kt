
package io.yoath.sports.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.yoath.sports.R
import io.yoath.sports.model.Organization
import io.yoath.sports.model.getTextView
import io.yoath.sports.utils.getSafe
import io.yoath.sports.utils.getSafeString
import io.yoath.sports.utils.log
import org.json.JSONObject


open class BaseListAdapter(var realmList: RealmList<Any>? = null, var layout: Int = R.layout.item_list_organization): RecyclerView.Adapter<OrgViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return OrgViewHolder(view)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: OrgViewHolder, position: Int) {
        println("binding hookup")
        realmList?.let {
            it[position]?.let { it1 -> holder.bind(it1 as JSONObject) }
        }
    }

}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * itemLocationName
     * detailsLinearLayout
     * addressLinearLayout
     * txtAddressOne
     * txtAddressTwo
     * txtCityStateZip
     * txtPeople
     * btnAddEditLocationManage
     * btnMinusLocationManage
     */

    var txtOrgName = itemView.findViewById<TextView>(R.id.itemLocationName)
    var txtAddressOne = itemView.getTextView(R.id.txtAddressOne)

//    fun bind(org: Organization) {
//        txtOrgName.text = org.name
//        txtAddressOne.text = org.id
//    }

    fun bind(org: JSONObject) {
        txtOrgName.text = org.getSafeString("name")
        txtAddressOne.text = org.getSafeString("id")
    }
}


