package net.typeblog.socks.util

import android.content.Context
import android.content.Intent
import android.util.Log
import net.typeblog.socks.BuildConfig.DEBUG
import net.typeblog.socks.R
import net.typeblog.socks.SocksVpnService
import net.typeblog.socks.System
import java.io.*

object Utility {
    private val TAG = Utility::class.java.simpleName

    fun extractFile(context: Context) {
        // Check app version
        val pref = context.getSharedPreferences("ver", Context.MODE_PRIVATE)

        var ver = 0
        try {
            ver = context.packageManager.getPackageInfo("net.typeblog.socks", 0).versionCode
        } catch (e: Exception) {
            throw RuntimeException(e)
            //return;
        }

        if (pref.getInt("ver", -1) == ver) {
            return
        }

        val target = Constants.DIR

        if (DEBUG) {
            Log.d(TAG, "target = $target")
        }

        if (File("$target/tun2socks").exists()) {
            File("$target/tun2socks").delete()
        }

        if (File("$target/pdnsd").exists()) {
            File("$target/pdnsd").delete()
        }

        File(target).mkdir()

        val source = System.abi

        val m = context.assets

        var files: Array<String>? = null
        try {
            files = m.list(source)
        } catch (e: IOException) {

        }

        if (files == null || files.size == 0) {
            return
        }

        for (f in files) {
            var input: InputStream? = null
            var out: OutputStream? = null

            try {
                input = m.open(source + "/" + f)
                out = FileOutputStream("$target/$f")

                input.copyTo(out)

                input!!.close()
                out.flush()
                out.close()

                exec(String.format("chmod 755 %s/%s", target, f))
            } catch (e: Exception) {

            }

        }

        pref.edit().putInt("ver", ver).commit()

    }

    fun exec(cmd: String): Int {
        try {
            val p = Runtime.getRuntime().exec(cmd)


            return p.waitFor()
        } catch (e: Exception) {
            return -1
        }

    }

    fun exec(cmdarray: Array<String>): Int {
        try {
            val p = Runtime.getRuntime().exec(cmdarray)


            return p.waitFor()
        } catch (e: Exception) {
            return -1
        }

    }

    fun killPidFile(f: String) {
        val file = File(f)

        if (!file.exists()) {
            return
        }

        var i: InputStream? = null
        try {
            i = FileInputStream(file)
        } catch (e: Exception) {
            return
        }

        var str: String

        try {
            str = i.bufferedReader().use { it.readText() }
            i.close()
        } catch (e: Exception) {
            return
        }

        try {
            val pid = Integer.parseInt(str.trim { it <= ' ' }.replace("\n", ""))
            Runtime.getRuntime().exec("kill $pid").waitFor()
            file.delete()
        } catch (e: Exception) {

        }

    }

    fun join(list: List<String>, separator: String): String {
        val ret = StringBuilder()

        for (s in list) {
            ret.append(s).append(separator)
        }

        return ret.substring(0, ret.length - separator.length)
    }

    fun makePdnsdConf(context: Context, dns: String, port: Int) {
        val conf = String.format(context.getString(R.string.pdnsd_conf), dns, port)

        val f = File("${Constants.DIR}/pdnsd.conf")

        if (f.exists()) {
            f.delete()
        }

        try {
            val out = FileOutputStream(f)
            out.write(conf.toByteArray())
            out.flush()
            out.close()
        } catch (e: Exception) {

        }

        val cache = File("${Constants.DIR}/pdnsd.cache")

        if (!cache.exists()) {
            try {
                cache.createNewFile()
            } catch (e: Exception) {

            }

        }
    }

    fun startVpn(context: Context, profile: Profile) {
        val i = Intent(context, SocksVpnService::class.java)
                .putExtra(Constants.INTENT_NAME, profile.name)
                .putExtra(Constants.INTENT_SERVER, profile.server)
                .putExtra(Constants.INTENT_PORT, profile.port)
                .putExtra(Constants.INTENT_ROUTE, profile.route)
                .putExtra(Constants.INTENT_DNS, profile.dns)
                .putExtra(Constants.INTENT_DNS_PORT, profile.dnsPort)
                .putExtra(Constants.INTENT_PER_APP, profile.isPerApp)
                .putExtra(Constants.INTENT_IPV6_PROXY, profile.hasIPv6())

        if (profile.isUserPw) {
            i.putExtra(Constants.INTENT_USERNAME, profile.username)
                    .putExtra(Constants.INTENT_PASSWORD, profile.password)
        }

        if (profile.isPerApp) {
            i.putExtra(Constants.INTENT_APP_BYPASS, profile.isBypassApp)
                    .putExtra(Constants.INTENT_APP_LIST, profile.appList!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        }

        if (profile.hasUDP()) {
            i.putExtra(Constants.INTENT_UDP_GW, profile.udpgw)
        }

        context.startService(i)
    }
}
