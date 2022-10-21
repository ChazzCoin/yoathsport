package io.yoath.sports.foodtruckManager.dashboard

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import io.yoath.sports.R
import io.yoath.sports.model.Session
import io.yoath.sports.model.Spot
import io.yoath.sports.utils.inflate
import io.realm.RealmList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_locations.view.*

class FoodDashboardViewAdapter(mContext: Context) : RecyclerView.Adapter<FoodDashboardViewAdapter.InnerSpotViewHolder>() {

    var spotsList : RealmList<Spot>? = Session.session?.spots
    var arrayOfSpots : ArrayList<Spot> = ArrayList()
    var context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerSpotViewHolder {
        reloadSpots()
        return InnerSpotViewHolder(parent.inflate(R.layout.item_list_food_spots))
    }

    override fun onBindViewHolder(viewHolder: InnerSpotViewHolder, position: Int) {

        arrayOfSpots.let {
            it[position].let { it1 ->
                viewHolder.bind(it1)
            }
        }

        viewHolder.itemView.setOnLongClickListener{
            arrayOfSpots[position].let { it1 ->
                AlertDialog.Builder(context)
                    .setMessage(R.string.location_dialog_delete_confirmation)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        //TODO: DELETE SPOT
                        Session.removeSpot(it1)
                        this.reloadSpots()
                        this.notifyDataSetChanged()
                    }
                    .setNegativeButton(R.string.close, null)
                    .show()
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
//        if (arrayOfSpots.isEmpty()){
//            reloadSpots()
//        }
        arrayOfSpots.let {
            return it.size
        }
    }

    private fun reloadSpots(){
        this.spotsList = Session.session?.spots
        arrayOfSpots.clear()
        spotsList?.iterator()?.forEach { itLocation ->
            arrayOfSpots.add(itLocation)
        }
    }


    inner class InnerSpotViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
//                onClick(spots[adapterPosition])
            }


        }

        fun bind(spot: Spot) {
            //TODO: SETUP DESIGN HERE
            containerView.itemLocationName.text = spot.locationName
            containerView.txtAddressOne.text = spot.addressOne
            containerView.txtAddressTwo.text = spot.addressTwo
            containerView.txtCityStateZip.text = "${spot.city}, ${spot.state}, ${spot.zip}"
            containerView.txtPeople.text = spot.estPeople
        }
    }
}