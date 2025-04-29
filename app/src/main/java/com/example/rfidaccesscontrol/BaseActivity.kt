/*
 * RFID Access Control System
 * Copyright (C) 2025 o-a-a-w
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.example.rfidaccesscontrol

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Base activity that provides common functionality for all activities in the app
 * including the battery level indicator in the action bar.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var batteryReceiver: BroadcastReceiver? = null
    private var batteryTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBatteryMonitor()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Get the layout from the menu item
        val menuItem = menu.findItem(R.id.menu_battery)
        val actionView = menuItem?.actionView

        // Find the TextView in the layout
        batteryTextView = actionView?.findViewById(R.id.tvActionBarBattery)

        // Initial update
        updateBatteryLevel()

        // Allow subclasses to add their menu items
        return true
    }

    private fun setupBatteryMonitor() {
        // Initial battery level check
        updateBatteryLevel()

        // Register receiver for battery updates
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    updateBatteryLevelFromIntent(it)
                }
            }
        }

        registerReceiver(
            batteryReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    private fun updateBatteryLevel() {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        batteryStatus?.let {
            updateBatteryLevelFromIntent(it)
        }
    }

    private fun updateBatteryLevelFromIntent(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val batteryPct = if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            0
        }

        // Update battery icon based on level
        val batteryDrawable = ContextCompat.getDrawable(this, R.drawable.ic_battery)
        when {
            batteryPct <= 15 -> {
                batteryDrawable?.setTint(ContextCompat.getColor(this, R.color.error_dark))
            }
            batteryPct <= 30 -> {
                batteryDrawable?.setTint(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            }
            else -> {
                batteryDrawable?.setTint(ContextCompat.getColor(this, R.color.white))
            }
        }

        // Also check if charging
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val batteryText = if (isCharging) "$batteryPct% ⚡" else "$batteryPct%"

        // Update the ActionBar TextView
        batteryTextView?.setCompoundDrawablesWithIntrinsicBounds(batteryDrawable, null, null, null)
        batteryTextView?.text = batteryText
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryReceiver?.let {
            unregisterReceiver(it)
        }
    }
}