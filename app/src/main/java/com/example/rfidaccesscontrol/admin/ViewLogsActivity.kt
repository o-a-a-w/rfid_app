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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.adapters.AccessLogAdapter
import com.example.rfidaccesscontrol.database.AccessLogRepository
import com.example.rfidaccesscontrol.databinding.ActivityViewLogsBinding
import com.example.rfidaccesscontrol.models.AccessLog
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewLogsActivity : BaseActivity() {
    private lateinit var binding: ActivityViewLogsBinding
    private lateinit var accessLogRepository: AccessLogRepository
    private lateinit var logs: List<AccessLog>

    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accessLogRepository = AccessLogRepository(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnExportLogs.setOnClickListener {
            requestPermissionsAndExport()
        }

        loadLogs()
    }

    private fun loadLogs() {
        logs = accessLogRepository.getAllLogs()

        if (logs.isEmpty()) {
            binding.tvNoLogs.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoLogs.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@ViewLogsActivity)
                adapter = AccessLogAdapter(this@ViewLogsActivity, logs)
            }
        }
    }

    private fun requestPermissionsAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ uses scoped storage, no need for runtime permission
            exportLogsToCSV()
        } else {
            // For older Android versions, check and request permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                exportLogsToCSV()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportLogsToCSV()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission required to export logs",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun exportLogsToCSV() {
        try {
            // Create filename with timestamp
            val timeStamp = SimpleDateFormat("yy-MM-dd'T'HH-mm-ss", Locale.getDefault()).format(Date())
            val fileName = "RFID_Logs_$timeStamp.csv"

            // Get Downloads directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val file = File(downloadsDir, fileName)
            val fileWriter = FileWriter(file)

            // Write CSV header
            fileWriter.append("ID,Timestamp,Message\n")

            // Write log data
            for (log in logs) {
                fileWriter.append("${log.id},${log.timestamp},\"${log.message}\"\n")
            }

            fileWriter.flush()
            fileWriter.close()

            Toast.makeText(
                this,
                "Logs exported to Downloads/$fileName",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Export failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }
}