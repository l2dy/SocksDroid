package net.typeblog.socks.util

object Constants {
    const val DIR = "/data/data/net.typeblog.socks/files"

    const val ABI_DEFAULT = "armeabi-v7a"

    const val ROUTE_ALL = "all"
    const val ROUTE_CHN = "chn"

    const val CHANNEL_ID = "socksdroid_channel"

    const val INTENT_PREFIX = "SOCKS"
    const val INTENT_NAME = INTENT_PREFIX + "NAME"
    const val INTENT_SERVER = INTENT_PREFIX + "SERV"
    const val INTENT_PORT = INTENT_PREFIX + "PORT"
    const val INTENT_USERNAME = INTENT_PREFIX + "UNAME"
    const val INTENT_PASSWORD = INTENT_PREFIX + "PASSWD"
    const val INTENT_ROUTE = INTENT_PREFIX + "ROUTE"
    const val INTENT_DNS = INTENT_PREFIX + "DNS"
    const val INTENT_DNS_PORT = INTENT_PREFIX + "DNSPORT"
    const val INTENT_PER_APP = INTENT_PREFIX + "PERAPP"
    const val INTENT_APP_BYPASS = INTENT_PREFIX + "APPBYPASS"
    const val INTENT_APP_LIST = INTENT_PREFIX + "APPLIST"
    const val INTENT_IPV6_PROXY = INTENT_PREFIX + "IPV6"
    const val INTENT_UDP_GW = INTENT_PREFIX + "UDPGW"

    const val PREF = "profile"
    const val PREF_PROFILE = "profile"
    const val PREF_LAST_PROFILE = "last_profile"
    const val PREF_SERVER_IP = "server_ip"
    const val PREF_SERVER_PORT = "server_port"
    const val PREF_IPV6_PROXY = "ipv6_proxy"
    const val PREF_UDP_PROXY = "udp_proxy"
    const val PREF_UDP_GW = "udp_gw"
    const val PREF_AUTH_USERPW = "auth_userpw"
    const val PREF_AUTH_USERNAME = "auth_username"
    const val PREF_AUTH_PASSWORD = "auth_password"
    const val PREF_ADV_ROUTE = "adv_route"
    const val PREF_ADV_DNS = "adv_dns"
    const val PREF_ADV_DNS_PORT = "adv_dns_port"
    const val PREF_ADV_PER_APP = "adv_per_app"
    const val PREF_ADV_APP_BYPASS = "adv_app_bypass"
    const val PREF_ADV_APP_LIST = "adv_app_list"
    const val PREF_ADV_AUTO_CONNECT = "adv_auto_connect"
}
