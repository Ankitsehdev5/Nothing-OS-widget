package com.example.nothingmusicwidget

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var permissionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.tv_permission_status)
        permissionButton = findViewById(R.id.btn_grant_permission)

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = isNotificationServiceEnabled()
        statusText.text = if (enabled) {
            "NOTIFICATION ACCESS: GRANTED\nAdd the Nothing Music widget from your home screen."
        } else {
            "NOTIFICATION ACCESS: REQUIRED\nGrant access so the widget can read & control active music."
        }
        permissionButton.text = if (enabled) "OPEN SETTINGS" else "GRANT ACCESS"
        MusicWidgetProvider.updateAllWidgets(this)
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, MediaListenerService::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }
}
