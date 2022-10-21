package io.yoath.sports.locationManager.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import io.yoath.sports.R
import io.yoath.sports.model.Location
import io.yoath.sports.model.addUpdateLocationToFirebase
import io.yoath.sports.utils.createFieldErrorDialog
import kotlinx.android.synthetic.main.fragment_locations.view.*
import java.util.*

/**
 * Created by ChazzCoin : December 2019.
 */

class LocManageFragment : Fragment() {

    val _EDIT = 0
    val _DISPLAY = 1
    var MODE = _DISPLAY

    var locManageViewAdapter: LocManageViewAdapter? = null
    var updateLocationObj: Location? = null
    lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        rootView = inflater.inflate(R.layout.fragment_locations, container, false)
        //Recycler View
        rootView.recyclerViewLocationsList.layoutManager = GridLayoutManager(requireContext(), 1)
        rootView.recyclerViewLocationsList.addItemDecoration(DividerItemDecoration(context, 0))
        reloadLocAdapter()

        rootView.btnAddUpdateLocation.setOnClickListener{
            getLocationInfoFromUser()
            reloadLocAdapter()
        }
        rootView.btnCancelLocationEdit.setOnClickListener {
            toggleButtons()
            clearAllFields()
        }

        return rootView
    }

    fun reloadLocAdapter() {
        locManageViewAdapter = LocManageViewAdapter(requireContext(), this)
        this.rootView.recyclerViewLocationsList.adapter = locManageViewAdapter
    }

    private fun getLocationInfoFromUser() {
        //Grab Fields from User
        val locationObj = verifyAndSetLocationInfo()
        //Create Location Object
        locationObj?.let {
            val newLocation = Location(it.id, it.locationName,
                it.addressOne, it.addressTwo, it.city, it.state, it.zip, it.estPeople)
            newLocation.addUpdateLocationToFirebase(this)
            clearAllFields()
        } ?: kotlin.run {
            createFieldErrorDialog(requireActivity()).show()
        }
    }

    private fun verifyAndSetLocationInfo() : Location? {
        var uid = ""
        updateLocationObj?.let { uid = it.id!! }
        if (hasEmptyField()) return null
        return Location().apply {
            this.id = if (uid.isEmpty()) UUID.randomUUID().toString() else uid
            this.locationName = rootView.editLocationName?.text?.toString() ?: return null
            this.addressOne = rootView.editAddressOne?.text?.toString() ?: return null
            this.addressTwo = rootView.editAddressTwo?.text?.toString() ?: ""
            this.city = rootView.editCity?.text?.toString() ?: return null
            this.state = rootView.editState?.text?.toString() ?: return null
            this.zip = rootView.editZip?.text?.toString() ?: return null
            this.estPeople = rootView.editEstPeople?.text?.toString() ?: return null
            if (updateLocationObj != null && updateLocationObj!!.matches(this)) {
                //NO FIELDS HAVE CHANGED
                return null
            }
        }
    }

    private fun hasEmptyField() : Boolean {
        if (rootView.editLocationName?.text.isNullOrEmpty()) return true
        if (rootView.editAddressOne?.text.isNullOrEmpty()) return true
        if (rootView.editCity?.text.isNullOrEmpty()) return true
        if (rootView.editState?.text.isNullOrEmpty()) return true
        if (rootView.editZip?.text.isNullOrEmpty()) return true
        if (rootView.editEstPeople?.text.isNullOrEmpty()) return true
        return false
    }

    fun toggleButtons() {
       when (MODE) {
           _DISPLAY -> {
               //go into edit mode
               MODE = _EDIT
               rootView.btnAddUpdateLocation.text = "Update"
               rootView.btnCancelLocationEdit.text = "Cancel"
               rootView.btnCancelLocationEdit.visibility = View.VISIBLE
           }
           _EDIT -> {
               //go into display mode
               MODE = _DISPLAY
               rootView.btnAddUpdateLocation.text = "Add Location"
               rootView.btnCancelLocationEdit.visibility = View.INVISIBLE
           }
       }
    }

    fun fillAllFields(location: Location){
        updateLocationObj = location
        rootView.editLocationName.setText(location.locationName)
        rootView.editAddressOne.setText(location.addressOne)
        rootView.editAddressTwo.setText(location.addressTwo)
        rootView.editCity.setText(location.city)
        rootView.editState.setText(location.state)
        rootView.editZip.setText(location.zip)
        rootView.editEstPeople.setText(location.estPeople)
    }

    fun clearAllFields(){
        updateLocationObj = null
        rootView.editLocationName.text.clear()
        rootView.editAddressOne.text.clear()
        rootView.editAddressTwo.text.clear()
        rootView.editCity.text.clear()
        rootView.editState.text.clear()
        rootView.editZip.text.clear()
        rootView.editEstPeople.text.clear()
    }
}