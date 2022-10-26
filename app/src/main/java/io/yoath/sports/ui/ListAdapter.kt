
package io.yoath.sports.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.yoath.sports.R
import io.yoath.sports.db.FireDB
import io.yoath.sports.utils.*
import org.json.JSONObject


open class BaseListAdapter(var realmList: RealmList<Any>? = null): RecyclerView.Adapter<RouterViewHolder>() {

    var layout: Int = R.layout.item_list_organization
    var type: String = FireDB.ORGANIZATIONS

    constructor(realmList: RealmList<Any>?, layout: Int) : this() {
        this.realmList = realmList
        this.layout = layout
    }

    constructor(realmList: RealmList<Any>?, layout: Int, type: String) : this() {
        this.realmList = realmList
        this.layout = layout
        this.type = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RouterViewHolder(view, type)
    }

    override fun getItemCount(): Int {
        return realmList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RouterViewHolder, position: Int) {
        println("binding hookup")
        realmList?.let {
            it[position]?.let { it1 ->
                holder.bind(it1 as JSONObject)
            }
        }
    }

}

class RouterViewHolder(itemView: View, var type:String) : RecyclerView.ViewHolder(itemView) {
    fun bind(org: JSONObject) {
        when (type) {
            FireDB.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(org)
        }
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * detailsLinearLayout
     * addressLinearLayout
     */
    var itemLinearLayout = itemView.getLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.getTextView(R.id.itemLocationName)
    var txtAddressOne = itemView.getTextView(R.id.txtAddressOne)
    var txtAddressTwo = itemView.getTextView(R.id.txtAddressTwo)
    var txtCityStateZip = itemView.getTextView(R.id.txtCityStateZip)
    var txtPeople = itemView.getTextView(R.id.txtPeople)
    var btnAddEditLocationManage = itemView.getImageButton(R.id.btnAddEditLocationManage)
    var btnMinusLocationManage = itemView.getImageButton(R.id.btnMinusLocationManage)

    fun bind(org: JSONObject) {
        txtOrgName.text = org.getSafeString("name")
        txtAddressOne.text = org.getSafeString("addressOne")
        txtAddressTwo.text = org.getSafeString("addressTwo")
        txtCityStateZip.text = org.getSafeString("city")
        txtPeople.text = org.getSafeString("sport")

        itemLinearLayout.setOnClickListener {
            println("!!!!! ORGANIZATION CELL PRESSED!!!!!!!!!!!!")
        }
    }
}


