package io.yoath.sports.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.model.*
import io.yoath.sports.basicUser.dashboard.FoodDashboardViewAdapter
import io.yoath.sports.coachUser.dashboard.LocDashViewAdapter
import io.yoath.sports.utils.*
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


/**
 * Created by ChazzCoin : December 2019.
 */

class DashboardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var rootView : View
    private lateinit var database: DatabaseReference

    //FoodTruck Manager
    private var foodAdapter : FoodDashboardViewAdapter? = null

    //Location Manager
    private lateinit var eSpinSpotLocation : Spinner
    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private var spotAdapter : LocDashViewAdapter? = null
    private var organizationList : RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    private var locationNameList : ArrayList<String?> = ArrayList() // -> USED FOR INPUT DIALOG
    private var organizationMap : HashMap<Int, Organization> = HashMap()
    private var finalOrganization : Organization? = null

    private var spotList: RealmList<Spot> = RealmList()
    var arrayOfSpots : ArrayList<Spot> = ArrayList()

    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //RecyclerView Init Setup
        rootView.recyclerViewDashboard.layoutManager = GridLayoutManager(requireContext(), 1)
        rootView.recyclerViewDashboard.addItemDecoration(DividerItemDecoration(context, 0))

        userOrLogout(requireActivity()) {
            user = it
            setup(it)
        }

        //-> Global On Click Listeners
        rootView.btnLogout.setOnClickListener {
            //FOR TESTING/ADMIN WORK ONLY
//            startActivity(Intent(requireContext(), AdminActivity::class.java))
            if (Session.isLogged){
                createAskUserLogoutDialog(requireActivity()).show()
            }
        }

        return rootView
    }

    private fun setup(user: User) {
        if (AuthController.USER_AUTH == AuthTypes.BASIC_USER) { //FoodTruck Manager
            setupFoodManager(user)
        } else if (AuthController.USER_AUTH == AuthTypes.COACH_USER) { //Location Manager
            setupLocationManager(user)
        }
    }

    /** FOODTRUCK USER SETUP **/
    private fun setupFoodManager(user: User) {
        rootView.txtUserWelcome.text = user.name
        rootView.txtUserEmail.text = user.email
        rootView.linearLayoutSpinner.visibility = View.GONE
        foodAdapter = FoodDashboardViewAdapter(requireContext())
        rootView.recyclerViewDashboard.adapter = foodAdapter

//        user.getFoodtrucksFromFirebase(requireContext(), this)
    }
    /** LOCATION USER SETUP **/
    private fun setupLocationManager(user: User) {
        rootView.txtUserWelcome?.text = user.name
        rootView.txtUserEmail?.text = user.email
        //->Spinner
        locations {
            if (!it.isNullOrEmpty()) {
                organizationList = it
                prepareListOfLocations()
                eSpinAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    locationNameList
                )
                val id = it[0]?.id
                getSpotsFromFirebase(getMonthYearForFirebase(), id ?: "")
                spotAdapter = LocDashViewAdapter(requireContext(), requireActivity(), id ?: "")
                rootView.recyclerViewDashboard.adapter = spotAdapter
            }
        }
        eSpinSpotLocation = rootView.findViewById(R.id.spinLocations)
        eSpinSpotLocation.onItemSelectedListener = this
        eSpinSpotLocation.adapter = eSpinAdapter
    }

    private fun prepareListOfLocations() {
        for ((i, location) in organizationList.withIndex()){
            locationNameList.add(location.name)
            organizationMap[i] = location
        }
    }

    private fun prepareListOfSpots() {
        spotList.iterator().forEach { arrayOfSpots.add(it) }
    }

    private fun checkSpotsForReviews() {
        when (user.auth) {
            AuthTypes.BASIC_USER -> {
                rootView.btnReview.makeVisible()
                rootView.btnReview.setOnClickListener {
                    createReviewDialog(requireActivity(), spot = null).show()
                }
            }
            AuthTypes.COACH_USER -> {
                if (arrayOfSpots.isNullOrEmpty()) return
                for (spot in arrayOfSpots) {
                    if (spot.hasReview) continue
                    if (!spot.isOld()) continue
                    rootView.btnReview.makeVisible()
                    rootView.btnReview.setOnClickListener {
                        createReviewDialog(requireActivity(), spot = spot).show()
                    }
                }
            }
        }
    }

    fun verifyFoodtruck() {
        if (!foodtruckIsValid()) {
            createProfileDialog(requireActivity(), user).show()
        }
    }

    private fun foodtruckIsValid() : Boolean {
        val trucks = Session.session?.foodtrucks
        if (trucks.isNullOrEmpty()) return false
//        for (truck in trucks!!) {
//            if (truck.truckName.isNullOrEmpty()) return false
//            if (truck.truckType.isNullOrEmpty()) return false
//        }
        return true
    }

    /** Location Manager Spinner On Click Listener **/
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //match the name of this location with locations in users locations
        (parent?.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
        finalOrganization = organizationMap[position]
        val _id = finalOrganization?.id
        spotAdapter?.locationId = _id ?: ""
        spotAdapter?.notifyDataSetChanged()
        Log.d("Location From Spinner: ", finalOrganization.toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getSpotsFromFirebase(monthYear: String, locationId: String) {
        Log.d("SpotCalendarLocation: ", "getSpotsFromFirebase")
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.AREAS).child(FireHelper.ALABAMA)
            .child(FireHelper.BIRMINGHAM).child(FireHelper.SPOTS).child(monthYear)
            .orderByChild("locationUUID").equalTo(locationId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val spot: Spot? = ds.getValue(Spot::class.java)
                        spot?.let {
                            if (it.isOld()) return@let
                            if (spotList.contains(it)) return@let
                            //locationId == ""
                            spotList.add(it)
                        }
                    }
                    //init view
                    prepareListOfSpots()
                    spotAdapter?.arrayOfSpots = arrayOfSpots
                    spotAdapter?.notifyDataSetChanged()
                    checkSpotsForReviews()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showFailedToast(requireContext())
                }
            })
    }
}