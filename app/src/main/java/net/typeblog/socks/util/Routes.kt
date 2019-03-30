package net.typeblog.socks.util

import android.content.Context
import android.net.VpnService
import net.typeblog.socks.R

object Routes {
    fun addRoutes(context: Context, builder: VpnService.Builder, name: String) {
        val routes: Array<String>?
        when (name) {
            Constants.ROUTE_ALL -> routes = arrayOf("0.0.0.0/0")
            Constants.ROUTE_CHN -> routes = context.resources.getStringArray(R.array.simple_route)
            else -> routes = arrayOf("0.0.0.0/0")
        }

        for (r in routes!!) {
            val cidr = r.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // Cannot handle 127.0.0.0/8
            if (cidr.size == 2 && !cidr[0].startsWith("127")) {
                builder.addRoute(cidr[0], Integer.parseInt(cidr[1]))
            }
        }
    }
}
