package io.yoath.sports.model

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.room.PrimaryKey
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.RealmList
import io.yoath.sports.AuthController
import io.yoath.sports.R
import io.yoath.sports.utils.FireHelper
import io.yoath.sports.utils.makeGone
import io.yoath.sports.utils.showFailedToast
import io.yoath.sports.utils.showSuccess
import io.realm.RealmObject
import kotlinx.android.synthetic.main.dialog_review_layout.*
import java.util.*

/**
 * Created by ChazzCoin : Feb 2021.
 */
open class Review : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var score: Int = 999 // score 1 or 10
    var type: String? = ""
    var details: String = "" //
    var questions: RealmList<String>? = null
}

fun Review.addUpdateToFirebase(mContext:Context, spot: Spot? = null) {
    val database: DatabaseReference?
    database = FirebaseDatabase.getInstance().reference
    database.child(FireHelper.REVIEWS)
        .child(AuthController.USER_AUTH).child(AuthController.USER_UID)
        .child(this.id).setValue(this)
        .addOnSuccessListener {
            //TODO("HANDLE SUCCESS")
            showSuccess(mContext)
            spot?.addUpdateToFirebase(mContext)
        }.addOnCompleteListener {
            //TODO("HANDLE COMPLETE")
        }.addOnFailureListener {
            //TODO("HANDLE FAILURE")
            showFailedToast(mContext)
        }
}

fun createReviewDialog(activity: Activity, spot: Spot? = null) : Dialog {

    val dialog = Dialog(activity)
    dialog.setContentView(R.layout.dialog_review_layout)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    if (spot != null) {
        dialog.txtSpotNameReview.text = "Spot Location: ${spot?.locationName}"
        dialog.txtSpotDateReview.text = "Spot Date: ${spot?.date}"
    } else {
        dialog.layoutReviewTitleBody.makeGone()
    }

    //Extra Settings
    val goodCheck = dialog.findViewById(R.id.checkLunchProfile) as CheckBox
    val badCheck = dialog.findViewById(R.id.checkDinnerProfile) as CheckBox
    goodCheck.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) { badCheck.isChecked = false }
    }
    badCheck.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) { goodCheck.isChecked = false }
    }
    // Body Review Details
    val reviewDetails = dialog.findViewById(R.id.editReviewDetails) as TextView
    reviewDetails.hint = "Give Feedback"

    // On Clicks
    val submit = dialog.findViewById(R.id.btnSubmitReview) as Button
    val cancel = dialog.findViewById(R.id.btnCancelReview) as Button
    submit.setOnClickListener {
        if (!goodCheck.isChecked && !badCheck.isChecked) {
            //TODO: LET USER KNOW
            badCheck.setBackgroundColor(Color.RED)
            goodCheck.setBackgroundColor(Color.RED)
            return@setOnClickListener
        }
        val uid = UUID.randomUUID().toString()
        val score = if (goodCheck.isChecked) {10} else {1}
        val details = reviewDetails.text.toString()
        spot?.apply {
            this.hasReview = true
            this.reviewUUID = uid
            this.reviewScore = score
            this.reviewDetails = details
        }?.addUpdateToFirebase(mContext = activity)
        Review().apply {
            this.id = uid
            this.score = score
            this.details = details
        }.addUpdateToFirebase(mContext = activity, spot = spot)
        dialog.dismiss()
    }
    cancel.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}