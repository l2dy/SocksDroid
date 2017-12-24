package net.typeblog.socks.util

import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference
import java.util.*

internal class ProfileFactory private constructor(private val mContext: Context, private val mPref: SharedPreferences) {
    private val mMap = HashMap<String, WeakReference<Profile>>()

    fun getProfile(name: String): Profile {
        var pRef: WeakReference<Profile>? = mMap[name]

        var p = pRef?.get()

        if (p == null) {
            p = Profile(mContext, mPref, name)
            pRef = WeakReference(p)
            mMap.put(name, pRef)
        }

        return p
    }

    companion object {
        private var sInstance: ProfileFactory? = null

        fun getInstance(context: Context, pref: SharedPreferences): ProfileFactory {
            if (sInstance == null) {
                sInstance = ProfileFactory(context, pref)
            }

            return sInstance!!
        }
    }
}
