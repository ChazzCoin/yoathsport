package io.yoath.sports.locationManager.dashboard

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.yoath.sports.R
import io.yoath.sports.model.*
import io.yoath.sports.utils.inflate
import io.realm.RealmList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_food_spots.view.*

class LocDashViewAdapter(mContext: Context, var activity: Activity, var locationId: String)
    : RecyclerView.Adapter<LocDashViewAdapter.InnerDashViewHolder>() {

    var locationList : RealmList<Location>? = Session.session?.locations
    var arrayOfLocations : ArrayList<Location> = ArrayList()

    var spotsList : RealmList<Spot>? = RealmList<Spot>()
    var arrayOfSpots : ArrayList<Spot> = ArrayList()

    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerDashViewHolder {
        return InnerDashViewHolder(parent.inflate(R.layout.item_list_food_spots), activity)
    }

    override fun onBindViewHolder(viewHolder: InnerDashViewHolder, position: Int) {
        arrayOfSpots.let {
            it[position].let { it1 ->
                if (it1.locationUUID == locationId) {
                    viewHolder.containerView.visibility = View.VISIBLE
                    viewHolder.containerView.layoutParams = RecyclerView
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    viewHolder.bind(it1)
                } else {
                    viewHolder.containerView.visibility = View.GONE
                    viewHolder.containerView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        arrayOfSpots.let {
            return it.size
        }
    }

    inner class InnerDashViewHolder(override val containerView: View, val activity: Activity) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(spot: Spot) {
            containerView.txtItemSpotName.text = spot.locationName
            containerView.txtItemDate.text = spot.toFullDate()
            containerView.txtItemEstPeople.text = spot.estPeople
            containerView.txtItemCost.text = spot.toFullPrice()

            containerView.setOnClickListener {
                spot.createDetailsLocationDialog(activity = activity).show()
            }
        }
    }

}