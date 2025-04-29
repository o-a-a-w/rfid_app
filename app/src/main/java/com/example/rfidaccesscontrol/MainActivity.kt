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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.example.rfidaccesscontrol.admin.AdminAuthActivity
import com.example.rfidaccesscontrol.database.AccessLogRepository
import com.example.rfidaccesscontrol.databinding.ActivityMainBinding
import com.example.rfidaccesscontrol.rfid.RFIDScannerActivity

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accessLogRepository: AccessLogRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accessLogRepository = AccessLogRepository(this)
        setupClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Call super to set up the battery indicator
        super.onCreateOptionsMenu(menu)

        // Add any MainActivity-specific menu items here if needed

        return true
    }

    private fun setupClickListeners() {
        // Scan RFID button
        binding.btnScanRfid.setOnClickListener {
            val intent = Intent(this, RFIDScannerActivity::class.java)
            startActivity(intent)
        }

        // Admin button
        binding.btnAdmin.setOnClickListener {
            val intent = Intent(this, AdminAuthActivity::class.java)
            startActivity(intent)
        }
    }
}