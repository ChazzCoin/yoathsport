package io.yoath.sports.locationManager.manage

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.yoath.sports.R
import io.yoath.sports.model.Location
import io.yoath.sports.model.Session
import io.yoath.sports.model.createDeleteLocationDialog
import io.yoath.sports.utils.inflate
import io.realm.RealmList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_locations.view.*


class LocManageViewAdapter(mContext: Context, val locManageFragment: LocManageFragment)
    : RecyclerView.Adapter<LocManageViewAdapter.InnerLocationViewHolder>() {

    var locationList : RealmList<Location>? = Session.session?.locations
    var arrayOfLocations : ArrayList<Location> = ArrayList()
    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerLocationViewHolder {
        reloadLocations()
        return InnerLocationViewHolder(parent.inflate(R.layout.item_list_locations), locManageFragment = locManageFragment)
    }

    override fun onBindViewHolder(viewHolder: InnerLocationViewHolder, position: Int) {

        arrayOfLocations.let {
            it[position].let { it1 ->
                viewHolder.bind(it1)
            }
        }

    }

    override fun getItemCount(): Int {
        if (arrayOfLocations.isEmpty()){
            reloadLocations()
        }
        arrayOfLocations.let {
            return it.size
        }
    }

    private fun reloadLocations(){
        this.locationList = Session.session?.locations
        arrayOfLocations.clear()
        locationList?.iterator()?.forEach { itLocation ->
            arrayOfLocations.add(itLocation)
        }
    }

    inner class InnerLocationViewHolder(override val containerView: View,
                                        val locManageFragment: LocManageFragment
    ) :
        RecyclerView.ViewHolder(containerView), LayoutContainer  {

        fun bind(location: Location) {
            containerView.itemLocationName.text = location.locationName
            containerView.txtAddressOne.text = location.addressOne
            containerView.txtAddressTwo.text = location.addressTwo
            containerView.txtCityStateZip.text = "${location.city}, ${location.state}, ${location.zip}"
            containerView.txtPeople.text = location.estPeople

            containerView.btnAddEditLocationManage.setOnClickListener {
                locManageFragment.toggleButtons()
                if (locManageFragment.MODE == locManageFragment._EDIT) {
                    locManageFragment.fillAllFields(location)
                } else {
                    locManageFragment.clearAllFields()
                }
            }
            containerView.btnMinusLocationManage.setOnClickListener {
                location.createDeleteLocationDialog(locManageFragment).show()
            }
        }

    }
}