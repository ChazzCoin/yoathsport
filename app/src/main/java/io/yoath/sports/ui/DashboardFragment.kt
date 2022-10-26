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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.model.*
import io.yoath.sports.coachUser.dashboard.LocDashViewAdapter
import io.yoath.sports.utils.*
import io.realm.RealmList
import io.yoath.sports.db.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.collections.HashMap


/**
 * Created by ChazzCoin : December 2019.
 */

class DashboardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var rootView : View
    //Location Manager
    private lateinit var eSpinSpotLocation : Spinner
    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private var spotAdapter : LocDashViewAdapter? = null
    private var organizationList : RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    private var organizationMap : HashMap<Int, Organization> = HashMap()
    private var finalOrganization : Organization? = null

    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //RecyclerView Init Setup
        rootView.recyclerViewDashboard.layoutManager = GridLayoutManager(requireContext(), 1)
        rootView.recyclerViewDashboard.addItemDecoration(DividerItemDecoration(context, 0))

        userOrLogout(requireActivity()) {
            user = it
//            setup(it)
        }

        //-> Global On Click Listeners
        rootView.btnLogout.setOnClickListener {
            //FOR TESTING/ADMIN WORK ONLY
            if (Session.isLogged){
                createAskUserLogoutDialog(requireActivity()).show()
            }
        }

//        createReview()
        getCoaches()
//        getOrganizations()
        return rootView
    }

    //not working
    private suspend fun getOrganizations2() {
        val test = getAllDB(FireDB.ORGANIZATIONS)
        log(test.toString())
    }

    private fun getOrganizations() {
        firebase { it ->
            it.child(FireDB.ORGANIZATIONS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val result = dataSnapshot.value as? HashMap<*, *> // <ID, <String, Any>>
                        result?.getSafe("name")
                        val resultList = result?.toJsonRealmList()
                        resultList?.let { itRL ->
                            recyclerViewDashboard.initRealmList(itRL, requireContext())
                        }
                        log(resultList.toString())
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }

    private fun getCoaches() {
        firebase { it ->
            it.child(FireDB.USERS).orderByChild("auth").equalTo("coach")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val result = dataSnapshot.value as? HashMap<*, *> // <ID, <String, Any>>
                        result?.getSafe("name")
                        val resultList = result?.toJsonRealmList()
                        resultList?.let { itRL ->
                            recyclerViewDashboard.initRealmList(itRL, requireContext())
                        }
                        log(resultList.toString())
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }

    private fun createReview() {
        var rev = Review()
        rev.apply {
            this.id = newUUID()
            this.score = 4
            this.details = "us soccer"
            this.questions = RealmList(
                Question().apply { this.question = "Are you satisfied?" },
                Question().apply { this.question = "Does this coach work well with kids?" },
                Question().apply { this.question = "Does this coach work well with parents?" },
                Question().apply { this.question = "Is this coach Chace Zanaty?" })
        }
        addUpdateDB(FireDB.REVIEWS, rev.id!!, rev)
    }

    private fun createFirstOrg() {
        var org = Organization()
        org.apply {
            this.id = newUUID()
            this.sport = "soccer"
            this.city = "birmingham"
            this.name = "JohnnysBananas"
        }
        addUpdateDB(FireDB.ORGANIZATIONS, org.id.toString(), org)
    }

//    private fun setup(user: User) {
//        if (AuthController.USER_AUTH == AuthTypes.BASIC_USER) { //FoodTruck Manager
//            //setup User
//            println("to be setup...")
//        }
//    }


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

}