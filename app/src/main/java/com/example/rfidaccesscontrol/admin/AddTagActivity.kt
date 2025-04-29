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

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.R
import com.example.rfidaccesscontrol.database.TagsRepository
import com.example.rfidaccesscontrol.databinding.ActivityAddTagBinding
import com.example.rfidaccesscontrol.utils.RFIDUtils

class AddTagActivity : BaseActivity() {
    private lateinit var binding: ActivityAddTagBinding
    private lateinit var tagsRepository: TagsRepository

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var scannedTagId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsRepository = TagsRepository(this)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Check if NFC is available
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Create a PendingIntent that will be used to read NFC tags
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        setupClickListeners()

        // Process intent if activity was started with an NFC tag
        processIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let {
            it.enableForegroundDispatch(
                this,
                pendingIntent,
                null,
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun setupClickListeners() {
        binding.btnScanTag.setOnClickListener {
            Toast.makeText(this, "Place RFID tag near device", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            saveTag()
        }
    }

    private fun processIntent(intent: Intent) {
        when (intent.action) {
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                // Get the tag from the intent
                val tag = intent.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
                val tagId = RFIDUtils.getTagId(tag)

                // Process the tag ID
                if (tagId != null && RFIDUtils.isValidRFIDTag(tagId)) {
                    scannedTagId = tagId
                    showTagDetails()
                }
            }
        }
    }

    private fun showTagDetails() {
        binding.tvTagId.text = getString(R.string.tag_id, scannedTagId)
        binding.tvTagId.visibility = View.VISIBLE
        binding.tvApartmentLabel.visibility = View.VISIBLE
        binding.etApartmentCode.visibility = View.VISIBLE
        binding.layoutButtons.visibility = View.VISIBLE
    }

    private fun saveTag() {
        val apartmentCode = binding.etApartmentCode.text.toString().trim()

        if (apartmentCode.isEmpty()) {
            Toast.makeText(this, "Please enter an apartment code", Toast.LENGTH_SHORT).show()
            return
        }

        if (scannedTagId == null) {
            Toast.makeText(this, "No tag scanned", Toast.LENGTH_SHORT).show()
            return
        }

        val result = tagsRepository.insertTag(scannedTagId!!, apartmentCode)

        if (result > 0) {
            Toast.makeText(this, getString(R.string.tag_saved), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, getString(R.string.tag_error), Toast.LENGTH_SHORT).show()
        }
    }
}