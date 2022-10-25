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
import io.yoath.sports.basicUser.dashboard.UserDashboardViewAdapter
import io.yoath.sports.coachUser.dashboard.LocDashViewAdapter
import io.yoath.sports.utils.*
import io.realm.RealmList
import io.yoath.sports.db.addOrganization
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by ChazzCoin : December 2019.
 */

class DashboardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var rootView : View
    private lateinit var database: DatabaseReference

    //FoodTruck Manager
    private var foodAdapter : UserDashboardViewAdapter? = null

    //Location Manager
    private lateinit var eSpinSpotLocation : Spinner
    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private var spotAdapter : LocDashViewAdapter? = null
    private var organizationList : RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    private var locationNameList : ArrayList<String?> = ArrayList() // -> USED FOR INPUT DIALOG
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
        createFirstOrg()
        return rootView
    }

    private fun createFirstOrg() {
        var org = Organization()
        org.apply {
            this.id = getUUID()
            this.sport = "soccer"
            this.city = "birmingham"
            this.name = "USYSR"
        }
        addOrganization(org)
    }

    private fun setup(user: User) {
        if (AuthController.USER_AUTH == AuthTypes.BASIC_USER) { //FoodTruck Manager
            //setup User
            println("to be setup...")
        }
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

}