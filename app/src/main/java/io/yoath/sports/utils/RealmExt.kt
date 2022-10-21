package io.yoath.sports.utils

import android.app.Activity
import io.yoath.sports.model.*
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by ChazzCoin : December 2019.
 */



fun realm() : Realm {
    return Realm.getDefaultInstance()
}

//LAMBA FUNCTION -> Shortcut for realm().executeTransaction{ }
inline fun executeRealm(crossinline block: (Realm) -> Unit) {
    realm().executeTransaction { block(it) }
}


inline fun session(block: (Session) -> Unit) {
    Session.session?.let { block(it) }
}

inline fun userOrLogout(activity: Activity? = null, block: (User) -> Unit) {
    Session.user?.let { block(it) } ?: run { activity?.let { Session.restartApplication(it) } }
}

fun sessionAndUser(block: (Session, User) -> Unit) {
    session { itSession ->
        userOrLogout { itUser ->
            block(itSession, itUser)
        }
    }
}

inline fun spots(block: (Session, RealmList<Spot>) -> Unit) {
    Session.session?.spots?.let { block(Session.session!!, it) }
}

inline fun locations(block: (RealmList<Organization>) -> Unit) {
    Session.session?.organizations?.let { block(it) }
}

inline fun sessionFoodtrucks(block: (RealmList<Organization>) -> Unit) {
    Session.session?.foodtrucks?.let { block(it) }
}

