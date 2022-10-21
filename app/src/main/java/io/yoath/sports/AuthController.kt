package io.yoath.sports

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration
import io.yoath.sports.basicUser.MainFoodTruckManagerActivity
import io.yoath.sports.locationManager.MainLocationManagerActivity
import io.yoath.sports.model.*
import io.yoath.sports.ui.login.LoginActivity
import io.yoath.sports.utils.FireHelper
import io.yoath.sports.utils.isNullOrEmpty
import io.yoath.sports.utils.showFailedToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


/**
 * Created by ChazzCoin : October 2022.
 * release key pw: M4BOfzfVr4ymXwI
 */

class AuthController : AppCompatActivity()  {

    companion object {
        var USER_UID = ""
        var USER_AUTH = ""
    }

    private val mInstance: AuthController? = null
    private lateinit var database: DatabaseReference
    val main = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var mUser : User? = null
    lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        //Init Firebase Instance
        FirebaseApp.initializeApp(this)
        //Init Calendar
        AndroidThreeTen.init(this)
        //Init Realm DB
        Realm.init(this)
        val realmConfiguration = RealmConfiguration.Builder()
            .name(BuildConfig.APPLICATION_ID + ".realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        //Finally . . .
        startActivity(Intent(this@AuthController, MainFoodTruckManagerActivity::class.java))
//        doSetup()
    }

    private fun doSetup() {
        val u = Session.user?.uid
        if (Session.session != null && Session.user != null && !u.isNullOrEmpty()){
            mUser = Session.user
            mUser?.let { itUser ->
                uid = itUser.uid
                USER_UID = itUser.uid
                USER_AUTH = itUser.auth.toString()
                //Init Cart
                createCart()
                getProfileUpdatesFirebase()
            }
        } else {
            //Create User Object (checks if null)
            Session.createUser()
            startActivity(Intent(this@AuthController, LoginActivity::class.java))
        }
    }

    @Synchronized
    fun getInstance(): AuthController? {
        return mInstance
    }

    override fun onRestart() {
        super.onRestart()
        doSetup()
    }

    private fun getProfileUpdatesFirebase() {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.PROFILES).child(FireHelper.USERS).child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val temp: User? = dataSnapshot.getValue(User::class.java) as User?
                    temp?.let { it ->
                        if (it.uid == uid){
                            USER_AUTH = it.auth.toString()
                            USER_UID = it.uid
                            Session.updateUser(it)
                            navigateUser(it)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    showFailedToast(this@AuthController)
                }
            })
    }

    private fun navigateUser(user: User){
        startActivity(Intent(this@AuthController, MainFoodTruckManagerActivity::class.java))
//        when (user.auth) {
//            AuthTypes.BASIC_USER -> {
//                startActivity(Intent(this@AuthController, MainFoodTruckManagerActivity::class.java))
//            }
//            AuthTypes.COACH_USER -> {
//                startActivity(Intent(this@AuthController, MainLocationManagerActivity::class.java))
//            }
//            else -> {
//                Toast.makeText(this, "Pending User Approval", Toast.LENGTH_LONG).show()
//                //TODO: CREATE TEMP PAGE FOR WAITING USERS
//                startActivity(Intent(this@AuthController, MainPendingActivity::class.java))
//            }
//        }
    }

}
