package io.yoath.sports.model

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import io.yoath.sports.AuthController
import io.yoath.sports.utils.executeRealm
import io.yoath.sports.utils.session
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by ChazzCoin : October 2022.
 */
open class Session : RealmObject() {
    //DO NOT MAKE STATIC!
    @PrimaryKey
    var sessionId = 0

    //DO NOT MAKE STATIC
    var organizations: RealmList<Organization>? = RealmList()

    /** -> EVERYTHING IS STATIC BELOW THIS POINT <- **/
    companion object {
        //SpotController

        //UserController
        private const val WAITING = "waiting"

        /** -> Controller Methods <- >  */
        private const val aisession = 1
        var USER_UID = ""

        //Class Variables
        private var mRealm = Realm.getDefaultInstance()

        //GET CURRENT SESSION
        var session: Session? = null
            get() {
                try {
                    if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                    field = mRealm.where(Session::class.java).equalTo("sessionId", aisession).findFirst()
                    if (field == null) {
                        field = Session()
                        field?.sessionId = aisession
                    }
                } catch (e: Exception) { e.printStackTrace() }
                return field
            }
            private set

        //GET CURRENT USER
        var user: User? = null
            get() {
                try {
                    if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                    field = mRealm.where(User::class.java).findFirst()
                    if (field == null) { field = User() }
                } catch (e: Exception) { e.printStackTrace() }
                return field
            }
            private set

        fun updateSession(user: User?): Session? {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            //Guest guest = SessionsController.getSession().getGuest();
            val session = session
            Log.e("loggedUser", "_wait__")
            session { itSession ->
                executeRealm { it.insertOrUpdate(itSession) }
            }
            return session
        }

        fun createUser() {
            val realm = Realm.getDefaultInstance()
            if (realm.where(User::class.java) == null){
                realm.executeTransaction { itRealm ->
                    itRealm.createObject(User::class.java)
                }
            }
        }

        fun updateUser(newNser: User){
            val curUser = user
            executeRealm { itRealm ->
                curUser?.uid = newNser.uid
                curUser?.auth = newNser.auth
                curUser?.name = newNser.name
                curUser?.email = newNser.email
                curUser?.phone = newNser.phone
                itRealm.insertOrUpdate(curUser)
            }
        }

//        fun updateSpotAsPendingForFirebase(id:String, status: String,
//                                           assignedTruckUid: String, assignedTruckName: String){
//            spots { session, spots ->
//                for (s in spots) {
//                    if (s.id == id) {
//                        executeRealm {
//                            s.assignedTruckName = assignedTruckName
//                            s.assignedTruckUid = assignedTruckUid
//                            s.status = status
//                            it.insertOrUpdate(session)
//                        }
//                    }
//                }
//            }
//        }

//        fun updateSpotAsBookedForFirebase(id:String) {
//            spots { session, spots ->
//                executeRealm { itRealm ->
//                    val s = spots.find { it.id == id }
//                    s?.status = Spot.BOOKED
//                    itRealm.insertOrUpdate(session)
//                }
//            }
//        }

//        fun updateSpotAsAvailableForFirebase(id:String) {
//            spots { session, spots ->
//                executeRealm {
//                    val s = spots.find { it.id == id }
//                    s?.assignedTruckName = ""
//                    s?.assignedTruckUid = ""
//                    s?.status = Spot.AVAILABLE
//                    it.insertOrUpdate(session)
//                }
//            }
//        }

        //CHECK IS USER IS LOGGED IN
        val isLogged: Boolean
            get() {
                if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                val session = session
                if (session != null && session.isValid) {
                    val user = Session.user
                    if (user != null && user.isValid) {
                        return true
                    }
                }
                return false
            }

        //LOG CURRENT USER OUT
        fun logOut() {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            mRealm.executeTransaction {
                mRealm.where(Session::class.java).findAll().deleteAllFromRealm()
                mRealm.where(User::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Organization::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Organization::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Cart::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Spot::class.java).findAll().deleteAllFromRealm()
            }
        }

        //SYSTEM RESTART THE APP
        fun restartApplication(context: Activity) {
            logOut()
            ActivityCompat.finishAffinity(context)
            context.startActivity(Intent(context, AuthController::class.java))
        }

        /** -> LOCATIONS <- **/

        //ADD LOCATION, this should be safe
        fun addOrganization(organization: Organization?) {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.add(organization)
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //REMOVE LOCATION
        fun removeOrganization(organization: Organization?) {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.remove(organization)
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //REMOVE ALL LOCATIONS
        fun removeAllLocations() {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.clear()
                mRealm.where(Organization::class.java).findAll().deleteAllFromRealm()
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }


        /** -> SPOTS <- **/

        fun createNewSpot(spot: Spot){
            mRealm?.let {
                it.beginTransaction()
                it.insert(spot)
                it.commitTransaction()
            }
        }


//        //SET SPOT LIST
//        fun setListOfSpots() {
//            try {
//                if (mRealm == null) {
//                    mRealm = Realm.getDefaultInstance()
//                }
//                mRealm?.let {
//                    val spotsResults = it.where(Spot::class.java).equalTo("spotManager", AuthController.USER_UID).findAll()
//                    listOfSpots.addAll(spotsResults.subList(0, spotsResults.size))
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

    }
}


