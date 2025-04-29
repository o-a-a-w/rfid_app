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
package com.example.rfidaccesscontrol.admin

import android.content.Intent
import android.os.Bundle
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnViewLogs.setOnClickListener {
            startActivity(Intent(this, ViewLogsActivity::class.java))
        }

        binding.btnAddTag.setOnClickListener {
            startActivity(Intent(this, AddTagActivity::class.java))
        }

        binding.btnDeleteTag.setOnClickListener {
            startActivity(Intent(this, DeleteTagActivity::class.java))
        }

        binding.btnViewTags.setOnClickListener {
            startActivity(Intent(this, ViewTagsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }
    }
}