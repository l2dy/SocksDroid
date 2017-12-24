package net.typeblog.socks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.util.Log
import net.typeblog.socks.BuildConfig.DEBUG
import net.typeblog.socks.util.ProfileManager
import net.typeblog.socks.util.Utility

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val p = ProfileManager.getInstance(context).default

        if (p != null && p.autoConnect() && VpnService.prepare(context) == null) {

            if (DEBUG) {
                Log.d(TAG, "starting VPN service on boot")
            }

            Utility.startVpn(context, p)
        }
    }

    companion object {
        private val TAG = BootReceiver::class.java.simpleName
    }
}
