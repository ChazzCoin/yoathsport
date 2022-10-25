package io.yoath.sports.utils

import android.app.Activity
import io.yoath.sports.model.*
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by ChazzCoin : December 2019.
 */

/** -> TRIED AND TRUE! <- */
fun <T> RealmList<T>?.toMutableList() : MutableList<T> {
    val listOfT = mutableListOf<T>()
    this?.let {
        for (item in it) {
            listOfT.add(item)
        }
    }
    return listOfT
}


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



inline fun locations(block: (RealmList<Organization>) -> Unit) {
    Session.session?.organizations?.let { block(it) }
}


