package net.typeblog.socks

import android.app.Activity
import android.os.Bundle
import net.typeblog.socks.util.Utility

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utility.extractFile(this)

        fragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment()).commit()
    }
}
