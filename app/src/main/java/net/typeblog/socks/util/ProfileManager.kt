package net.typeblog.socks.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import net.typeblog.socks.R
import java.util.*

@SuppressLint("ApplySharedPref")
class ProfileManager private constructor(private val mContext: Context) {

    private val mPref: SharedPreferences
    private val mFactory: ProfileFactory
    private val mProfiles = ArrayList<String>()

    val profiles: Array<String>
        get() = mProfiles.toTypedArray()

    val default: Profile?
        get() = getProfile(mPref.getString(Constants.PREF_LAST_PROFILE, mProfiles[0]))

    init {
        mPref = mContext.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE)
        mFactory = ProfileFactory.getInstance(mContext, mPref)
        reload()
    }

    fun reload() {
        mProfiles.clear()
        mProfiles.add(mContext.getString(R.string.prof_default))

        val profiles = mPref.getString(Constants.PREF_PROFILE, "")!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (p in profiles) {
            if (!TextUtils.isEmpty(p)) {
                mProfiles.add(p)
            }
        }
    }

    fun getProfile(name: String): Profile? {
        return if (!mProfiles.contains(name)) {
            null
        } else {
            mFactory.getProfile(name)
        }
    }

    fun switchDefault(name: String) {
        if (mProfiles.contains(name))
            mPref.edit().putString(Constants.PREF_LAST_PROFILE, name).commit()
    }

    fun addProfile(name: String): Profile? {
        if (mProfiles.contains(name)) {
            return null
        } else {
            mProfiles.add(name)
            mProfiles.removeAt(0)
            mPref.edit().putString(Constants.PREF_PROFILE, Utility.join(mProfiles, "\n"))
                    .putString(Constants.PREF_LAST_PROFILE, name).commit()
            reload()
            return default
        }
    }

    fun removeProfile(name: String): Boolean {
        if (name === mProfiles[0] || !mProfiles.contains(name)) {
            return false
        }

        getProfile(name)!!.delete()

        mProfiles.removeAt(0)
        mProfiles.remove(name)

        mPref.edit().putString(Constants.PREF_PROFILE, Utility.join(mProfiles, "\n"))
                .remove(Constants.PREF_LAST_PROFILE).commit()
        reload()

        return true
    }

    companion object {
        private var sInstance: ProfileManager? = null

        fun getInstance(context: Context): ProfileManager {
            if (sInstance == null) {
                sInstance = ProfileManager(context)
            }

            return sInstance!!
        }
    }
}
