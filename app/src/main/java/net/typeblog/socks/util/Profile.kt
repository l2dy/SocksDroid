package net.typeblog.socks.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("ApplySharedPref")
class Profile internal constructor(private val mContext: Context, private val mPref: SharedPreferences, val name: String) {
    private val mPrefix: String

    var server: String
        get() = mPref.getString(key("server"), "127.0.0.1")
        set(server) {
            mPref.edit().putString(key("server"), server).commit()
        }

    var port: Int
        get() = mPref.getInt(key("port"), 1080)
        set(port) {
            mPref.edit().putInt(key("port"), port).commit()
        }

    val isUserPw: Boolean
        get() = mPref.getBoolean(key("userpw"), false)

    var username: String
        get() = mPref.getString(key("username"), "")
        set(username) {
            mPref.edit().putString(key("username"), username).commit()
        }

    var password: String
        get() = mPref.getString(key("password"), "")
        set(password) {
            mPref.edit().putString(key("password"), password).commit()
        }

    var route: String
        get() = mPref.getString(key("route"), Constants.ROUTE_ALL)
        set(route) {
            mPref.edit().putString(key("route"), route).commit()
        }

    var dns: String
        get() = mPref.getString(key("dns"), "8.8.8.8")
        set(dns) {
            mPref.edit().putString(key("dns"), dns).commit()
        }

    var dnsPort: Int
        get() = mPref.getInt(key("dns_port"), 53)
        set(port) {
            mPref.edit().putInt(key("dns_port"), port).commit()
        }

    var isPerApp: Boolean
        get() = mPref.getBoolean(key("perapp"), false)
        set(`is`) {
            mPref.edit().putBoolean(key("perapp"), `is`).commit()
        }

    var isBypassApp: Boolean
        get() = mPref.getBoolean(key("appbypass"), false)
        set(`is`) {
            mPref.edit().putBoolean(key("appbypass"), `is`).commit()
        }

    var appList: String
        get() = mPref.getString(key("applist"), "")
        set(list) {
            mPref.edit().putString(key("applist"), list).commit()
        }

    var udpgw: String
        get() = mPref.getString(key("udpgw"), "127.0.0.1:7300")
        set(gw) {
            mPref.edit().putString(key("udpgw"), gw).commit()
        }

    init {
        mPrefix = prefPrefix(name)
    }

    fun setIsUserpw(`is`: Boolean) {
        mPref.edit().putBoolean(key("userpw"), `is`).commit()
    }

    fun hasIPv6(): Boolean {
        return mPref.getBoolean(key("ipv6"), false)
    }

    fun setHasIPv6(has: Boolean) {
        mPref.edit().putBoolean(key("ipv6"), has).commit()
    }

    fun hasUDP(): Boolean {
        return mPref.getBoolean(key("udp"), false)
    }

    fun setHasUDP(has: Boolean) {
        mPref.edit().putBoolean(key("udp"), has).commit()
    }

    fun autoConnect(): Boolean {
        return mPref.getBoolean(key("auto"), false)
    }

    fun setAutoConnect(auto: Boolean) {
        mPref.edit().putBoolean(key("auto"), auto).commit()
    }

    internal fun delete() {
        mPref.edit()
                .remove(key("server"))
                .remove(key("port"))
                .remove(key("userpw"))
                .remove(key("username"))
                .remove(key("password"))
                .remove(key("route"))
                .remove(key("dns"))
                .remove(key("dns_port"))
                .remove(key("perapp"))
                .remove(key("appbypass"))
                .remove(key("applist"))
                .remove(key("ipv6"))
                .remove(key("udp"))
                .remove(key("udpgw"))
                .remove(key("auto"))
                .commit()
    }

    private fun key(k: String): String {
        return mPrefix + k
    }

    private fun prefPrefix(name: String): String {
        return name.replace("_", "__").replace(" ", "_")
    }
}
