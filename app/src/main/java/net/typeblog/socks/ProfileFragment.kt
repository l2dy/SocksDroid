package net.typeblog.socks

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.preference.*
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import net.typeblog.socks.util.Constants
import net.typeblog.socks.util.Profile
import net.typeblog.socks.util.ProfileManager
import net.typeblog.socks.util.Utility

class ProfileFragment : PreferenceFragment(), Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, CompoundButton.OnCheckedChangeListener {
    private var mManager: ProfileManager? = null
    private var mProfile: Profile? = null

    private var mSwitch: Switch? = null
    private var mRunning = false
    private var mStarting = false
    private var mStopping = false
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p1: ComponentName, binder: IBinder) {
            mBinder = IVpnService.Stub.asInterface(binder)

            try {
                mRunning = mBinder!!.isRunning
            } catch (e: Exception) {

            }

            if (mRunning) {
                updateState()
            }
        }

        override fun onServiceDisconnected(p1: ComponentName) {
            mBinder = null
        }
    }
    private val mStateRunnable = object : Runnable {
        override fun run() {
            updateState()
            mSwitch!!.postDelayed(this, 1000)
        }
    }
    private var mBinder: IVpnService? = null

    private var mPrefProfile: ListPreference? = null
    private var mPrefRoutes: ListPreference? = null
    private var mPrefServer: EditTextPreference? = null
    private var mPrefPort: EditTextPreference? = null
    private var mPrefUsername: EditTextPreference? = null
    private var mPrefPassword: EditTextPreference? = null
    private var mPrefDns: EditTextPreference? = null
    private var mPrefDnsPort: EditTextPreference? = null
    private var mPrefAppList: EditTextPreference? = null
    private var mPrefUDPGW: EditTextPreference? = null
    private var mPrefUserpw: CheckBoxPreference? = null
    private var mPrefPerApp: CheckBoxPreference? = null
    private var mPrefAppBypass: CheckBoxPreference? = null
    private var mPrefIPv6: CheckBoxPreference? = null
    private var mPrefUDP: CheckBoxPreference? = null
    private var mPrefAuto: CheckBoxPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        setHasOptionsMenu(true)
        mManager = ProfileManager.getInstance(activity.applicationContext)
        initPreferences()
        reload()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)

        val s = menu.findItem(R.id.switch_main)
        mSwitch = s.actionView.findViewById(R.id.switch_action_button)
        mSwitch!!.setOnCheckedChangeListener(this)
        mSwitch!!.postDelayed(mStateRunnable, 1000)
        checkState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.prof_add -> {
                addProfile()
                return true
            }
            R.id.prof_del -> {
                removeProfile()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPreferenceClick(p: Preference): Boolean {
        // TODO: Implement this method
        return false
    }

    override fun onPreferenceChange(p: Preference, newValue: Any): Boolean {
        if (p === mPrefProfile) {
            val name = newValue.toString()
            mProfile = mManager!!.getProfile(name)
            mManager!!.switchDefault(name)
            reload()
            return true
        } else if (p === mPrefServer) {
            mProfile!!.server = newValue.toString()
            resetTextN(mPrefServer, newValue)
            return true
        } else if (p === mPrefPort) {
            if (TextUtils.isEmpty(newValue.toString()))
                return false

            mProfile!!.port = Integer.parseInt(newValue.toString())
            resetTextN(mPrefPort, newValue)
            return true
        } else if (p === mPrefUserpw) {
            mProfile!!.setIsUserpw(java.lang.Boolean.parseBoolean(newValue.toString()))
            return true
        } else if (p === mPrefUsername) {
            mProfile!!.username = newValue.toString()
            resetTextN(mPrefUsername, newValue)
            return true
        } else if (p === mPrefPassword) {
            mProfile!!.password = newValue.toString()
            resetTextN(mPrefPassword, newValue)
            return true
        } else if (p === mPrefRoutes) {
            mProfile!!.route = newValue.toString()
            resetListN(mPrefRoutes, newValue)
            return true
        } else if (p === mPrefDns) {
            mProfile!!.dns = newValue.toString()
            resetTextN(mPrefDns, newValue)
            return true
        } else if (p === mPrefDnsPort) {
            if (TextUtils.isEmpty(newValue.toString()))
                return false

            mProfile!!.dnsPort = Integer.valueOf(newValue.toString())!!
            resetTextN(mPrefDnsPort, newValue)
            return true
        } else if (p === mPrefPerApp) {
            mProfile!!.isPerApp = java.lang.Boolean.parseBoolean(newValue.toString())
            return true
        } else if (p === mPrefAppBypass) {
            mProfile!!.isBypassApp = java.lang.Boolean.parseBoolean(newValue.toString())
            return true
        } else if (p === mPrefAppList) {
            mProfile!!.appList = newValue.toString()
            return true
        } else if (p === mPrefIPv6) {
            mProfile!!.setHasIPv6(java.lang.Boolean.parseBoolean(newValue.toString()))
            return true
        } else if (p === mPrefUDP) {
            mProfile!!.setHasUDP(java.lang.Boolean.parseBoolean(newValue.toString()))
            return true
        } else if (p === mPrefUDPGW) {
            mProfile!!.udpgw = newValue.toString()
            resetTextN(mPrefUDPGW, newValue)
            return true
        } else if (p === mPrefAuto) {
            mProfile!!.setAutoConnect(java.lang.Boolean.parseBoolean(newValue.toString()))
            return true
        } else {
            return false
        }
    }

    override fun onCheckedChanged(p1: CompoundButton, checked: Boolean) {
        if (checked) {
            startVpn()
        } else {
            stopVpn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Utility.startVpn(activity, mProfile!!)
            checkState()
        }
    }

    private fun initPreferences() {
        mPrefProfile = findPreference(Constants.PREF_PROFILE) as ListPreference
        mPrefServer = findPreference(Constants.PREF_SERVER_IP) as EditTextPreference
        mPrefPort = findPreference(Constants.PREF_SERVER_PORT) as EditTextPreference
        mPrefUserpw = findPreference(Constants.PREF_AUTH_USERPW) as CheckBoxPreference
        mPrefUsername = findPreference(Constants.PREF_AUTH_USERNAME) as EditTextPreference
        mPrefPassword = findPreference(Constants.PREF_AUTH_PASSWORD) as EditTextPreference
        mPrefRoutes = findPreference(Constants.PREF_ADV_ROUTE) as ListPreference
        mPrefDns = findPreference(Constants.PREF_ADV_DNS) as EditTextPreference
        mPrefDnsPort = findPreference(Constants.PREF_ADV_DNS_PORT) as EditTextPreference
        mPrefPerApp = findPreference(Constants.PREF_ADV_PER_APP) as CheckBoxPreference
        mPrefAppBypass = findPreference(Constants.PREF_ADV_APP_BYPASS) as CheckBoxPreference
        mPrefAppList = findPreference(Constants.PREF_ADV_APP_LIST) as EditTextPreference
        mPrefIPv6 = findPreference(Constants.PREF_IPV6_PROXY) as CheckBoxPreference
        mPrefUDP = findPreference(Constants.PREF_UDP_PROXY) as CheckBoxPreference
        mPrefUDPGW = findPreference(Constants.PREF_UDP_GW) as EditTextPreference
        mPrefAuto = findPreference(Constants.PREF_ADV_AUTO_CONNECT) as CheckBoxPreference

        mPrefProfile!!.onPreferenceChangeListener = this
        mPrefServer!!.onPreferenceChangeListener = this
        mPrefPort!!.onPreferenceChangeListener = this
        mPrefUserpw!!.onPreferenceChangeListener = this
        mPrefUsername!!.onPreferenceChangeListener = this
        mPrefPassword!!.onPreferenceChangeListener = this
        mPrefRoutes!!.onPreferenceChangeListener = this
        mPrefDns!!.onPreferenceChangeListener = this
        mPrefDnsPort!!.onPreferenceChangeListener = this
        mPrefPerApp!!.onPreferenceChangeListener = this
        mPrefAppBypass!!.onPreferenceChangeListener = this
        mPrefAppList!!.onPreferenceChangeListener = this
        mPrefIPv6!!.onPreferenceChangeListener = this
        mPrefUDP!!.onPreferenceChangeListener = this
        mPrefUDPGW!!.onPreferenceChangeListener = this
        mPrefAuto!!.onPreferenceChangeListener = this
    }

    private fun reload() {
        if (mProfile == null) {
            mProfile = mManager!!.default
        }

        mPrefProfile!!.entries = mManager!!.profiles
        mPrefProfile!!.entryValues = mManager!!.profiles
        mPrefProfile!!.value = mProfile!!.name
        mPrefRoutes!!.value = mProfile!!.route
        resetList(mPrefProfile!!, mPrefRoutes!!)

        mPrefUserpw!!.isChecked = mProfile!!.isUserPw
        mPrefPerApp!!.isChecked = mProfile!!.isPerApp
        mPrefAppBypass!!.isChecked = mProfile!!.isBypassApp
        mPrefIPv6!!.isChecked = mProfile!!.hasIPv6()
        mPrefUDP!!.isChecked = mProfile!!.hasUDP()
        mPrefAuto!!.isChecked = mProfile!!.autoConnect()

        mPrefServer!!.text = mProfile!!.server
        mPrefPort!!.text = mProfile!!.port.toString()
        mPrefUsername!!.text = mProfile!!.username
        mPrefPassword!!.text = mProfile!!.password
        mPrefDns!!.text = mProfile!!.dns
        mPrefDnsPort!!.text = mProfile!!.dnsPort.toString()
        mPrefUDPGW!!.text = mProfile!!.udpgw
        resetText(mPrefServer!!, mPrefPort!!, mPrefUsername!!, mPrefPassword!!, mPrefDns!!, mPrefDnsPort!!, mPrefUDPGW!!)

        mPrefAppList!!.text = mProfile!!.appList
    }

    private fun resetList(vararg pref: ListPreference) {
        for (p in pref)
            p.summary = p.entry
    }

    private fun resetListN(pref: ListPreference?, newValue: Any) {
        pref!!.summary = newValue.toString()
    }

    private fun resetText(vararg pref: EditTextPreference) {
        for (p in pref) {
            if (p.editText.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                p.summary = p.text
            } else {
                if (p.text.length > 0)
                    p.summary = String.format(String.format("%%0%dd", p.text.length), 0).replace("0", "*")
                else
                    p.summary = ""
            }
        }
    }

    private fun resetTextN(pref: EditTextPreference?, newValue: Any) {
        if (pref!!.editText.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            pref.summary = newValue.toString()
        } else {
            val text = newValue.toString()
            if (text.length > 0)
                pref.summary = String.format(String.format("%%0%dd", text.length), 0).replace("0", "*")
            else
                pref.summary = ""
        }
    }

    private fun addProfile() {
        val e = EditText(activity)
        e.setSingleLine(true)

        AlertDialog.Builder(activity)
                .setTitle(R.string.prof_add)
                .setView(e)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { d, which ->
                    val name = e.text.toString().trim { it <= ' ' }

                    if (!TextUtils.isEmpty(name)) {
                        val p = mManager!!.addProfile(name)

                        if (p != null) {
                            mProfile = p
                            reload()
                            return@OnClickListener
                        }
                    }

                    Toast.makeText(activity,
                            String.format(getString(R.string.err_add_prof), name),
                            Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton(android.R.string.cancel) { d, which -> }
                .create().show()
    }

    private fun removeProfile() {
        AlertDialog.Builder(activity)
                .setTitle(R.string.prof_del)
                .setMessage(String.format(getString(R.string.prof_del_confirm), mProfile!!.name))
                .setPositiveButton(android.R.string.ok) { d, which ->
                    if (!mManager!!.removeProfile(mProfile!!.name)) {
                        Toast.makeText(activity,
                                getString(R.string.err_del_prof, mProfile!!.name),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        mProfile = mManager!!.default
                        reload()
                    }
                }
                .setNegativeButton(android.R.string.cancel) { d, which -> }
                .create().show()
    }

    private fun checkState() {
        mRunning = false
        mSwitch!!.isEnabled = false
        mSwitch!!.setOnCheckedChangeListener(null)

        if (mBinder == null) {
            activity.bindService(Intent(activity, SocksVpnService::class.java), mConnection, 0)
        }
    }

    private fun updateState() {
        if (mBinder == null) {
            mRunning = false
        } else {
            try {
                mRunning = mBinder!!.isRunning
            } catch (e: Exception) {
                mRunning = false
            }

        }

        mSwitch!!.isChecked = mRunning

        if (!mStarting && !mStopping || mStarting && mRunning || mStopping && !mRunning) {
            mSwitch!!.isEnabled = true
        }

        if (mStarting && mRunning) {
            mStarting = false
        }

        if (mStopping && !mRunning) {
            mStopping = false
        }

        mSwitch!!.setOnCheckedChangeListener(this@ProfileFragment)
    }

    private fun startVpn() {
        mStarting = true
        val i = VpnService.prepare(activity)

        if (i != null) {
            startActivityForResult(i, 0)
        } else {
            onActivityResult(0, Activity.RESULT_OK, null)
        }
    }

    private fun stopVpn() {
        if (mBinder == null)
            return

        mStopping = true

        try {
            mBinder!!.stop()
        } catch (e: Exception) {

        }

        mBinder = null

        activity.unbindService(mConnection)
        checkState()
    }
}
