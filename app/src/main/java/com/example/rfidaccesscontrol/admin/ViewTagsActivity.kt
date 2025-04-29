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
import com.example.rfidaccesscontrol.adapters.TagListAdapter
import com.example.rfidaccesscontrol.database.TagsRepository
import com.example.rfidaccesscontrol.databinding.ActivityViewTagsBinding
import com.example.rfidaccesscontrol.models.Tag
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewTagsActivity : BaseActivity() {
    private lateinit var binding: ActivityViewTagsBinding
    private lateinit var tagsRepository: TagsRepository
    private lateinit var tags: List<Tag>

    private val PERMISSION_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTagsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsRepository = TagsRepository(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnExportTags.setOnClickListener {
            requestPermissionsAndExport()
        }

        loadTags()
    }

    private fun loadTags() {
        tags = tagsRepository.getAllTags()

        if (tags.isEmpty()) {
            binding.tvNoTags.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoTags.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@ViewTagsActivity)
                adapter = TagListAdapter(tags) // No delete callback here
            }
        }
    }

    private fun requestPermissionsAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ uses scoped storage, no need for runtime permission
            exportTagsToCSV()
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
                exportTagsToCSV()
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
                exportTagsToCSV()
            } else {
                Toast.makeText(
                    this,
                    "Storage permission required to export tags",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun exportTagsToCSV() {
        try {
            // Create filename with timestamp
            val timeStamp = SimpleDateFormat("yy-MM-dd'T'HH-mm-ss", Locale.getDefault()).format(Date())
            val fileName = "RFID_Tags_$timeStamp.csv"

            // Get Downloads directory
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val file = File(downloadsDir, fileName)
            val fileWriter = FileWriter(file)

            // Write CSV header
            fileWriter.append("ID,Tag ID,Apartment Code,Creation Date\n")

            // Write tag data
            for (tag in tags) {
                fileWriter.append("${tag.id},${tag.tagId},${tag.apartmentCode},${tag.creationDate}\n")
            }

            fileWriter.flush()
            fileWriter.close()

            Toast.makeText(
                this,
                "Tags exported to Downloads/$fileName",
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