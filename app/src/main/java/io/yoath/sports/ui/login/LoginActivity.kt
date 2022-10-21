package io.yoath.sports.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.model.Session
import io.yoath.sports.model.User
import io.yoath.sports.utils.FireHelper

/**
 * Created by ChazzCoin : December 2019.
 */

class LoginActivity : AppCompatActivity() {

    private var COUNT = 0
    private val RC_SIGN_IN: Int = 999

    private lateinit var database: DatabaseReference
    private lateinit var mUser : User

    // Choose authentication providers
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        database = FirebaseDatabase.getInstance().reference

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }
    //Ask for auth first -> if null add waiting
    private fun getUserAuthFromFirebase(currentUser : FirebaseUser) {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.PROFILES).child(FireHelper.USERS).child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.getValue(User::class.java)
                    mUser = User(currentUser.uid,currentUser.displayName,currentUser.email)
                    user?.let { itUser ->
                        mUser.auth = itUser.auth
                        mUser.phone = itUser.phone ?: ""
                        saveProfileToFirebase()
                    }?: kotlin.run {
                        //No User Move On
                        mUser.auth = User.WAITING
                        saveProfileToFirebase()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
//                    TODO("not implemented")
                }
            })
    }

//    private fun googleTesting() {
//        val signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            .build()
//    }

    //SAVE PROFILE
    private fun saveProfileToFirebase() {
        database.child(FireHelper.PROFILES).child(FireHelper.USERS).child(mUser.uid).setValue(mUser)
            .addOnSuccessListener {
//                TODO("HANDLE SUCCESS")
                Session.updateUser(mUser)
                Session.updateSession(mUser)
                startActivity(Intent(this@LoginActivity, AuthController::class.java))
            }.addOnCompleteListener {
//                TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
//                TODO("HANDLE FAILURE")
                showLoginFailed()
            }
    }

    private fun showLoginFailed() {
        Toast.makeText(applicationContext, "Error Signing In", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //TODO: SETUP USER MODEL
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    //Grab Auth from Firebase if available
                    getUserAuthFromFirebase(user)
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}


