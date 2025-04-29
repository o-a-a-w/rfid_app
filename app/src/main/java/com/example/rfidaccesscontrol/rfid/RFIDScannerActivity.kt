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
package com.example.rfidaccesscontrol.rfid

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedVectorDrawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.R
import com.example.rfidaccesscontrol.database.AccessLogRepository
import com.example.rfidaccesscontrol.database.TagsRepository
import com.example.rfidaccesscontrol.databinding.ActivityRfidScannerBinding
import com.example.rfidaccesscontrol.utils.AudioPlayer
import com.example.rfidaccesscontrol.utils.RFIDUtils

class RFIDScannerActivity : BaseActivity() {
    private lateinit var binding: ActivityRfidScannerBinding
    private lateinit var tagsRepository: TagsRepository
    private lateinit var accessLogRepository: AccessLogRepository
    private lateinit var audioPlayer: AudioPlayer

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsRepository = TagsRepository(this)
        accessLogRepository = AccessLogRepository(this)
        audioPlayer = AudioPlayer(this)

        // Start the scanning animation
        (binding.ivScanAnimation.drawable as? AnimatedVectorDrawable)?.start()

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Check if NFC is available
        if (nfcAdapter == null) {
            binding.tvScanInstructions.text = "NFC is not available on this device"
            return
        }

        // Create a PendingIntent that will be used to read NFC tags
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE
        )

        // Set up return button
        binding.btnReturn.setOnClickListener {
            finish()
        }

        // Process intent if activity was started with an NFC tag
        processIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let {
            it.enableForegroundDispatch(
                this,
                pendingIntent,
                arrayOf(IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)),
                null
            )
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.release()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        when (intent.action) {
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                // Get the tag from the intent
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                val tagId = RFIDUtils.getTagId(tag)

                // Process the tag ID
                if (tagId != null) {
                    checkAccess(tagId)
                }
            }
        }
    }

    private fun checkAccess(tagId: String) {
        // Check if tag exists in database
        val tag = tagsRepository.findTagById(tagId)

        if (tag != null) {
            // Access granted
            showAccessGranted(tag.apartmentCode)
            accessLogRepository.insertLog("ACCESS GRANTED - ${tag.tagId} - ${tag.apartmentCode}")
            audioPlayer.playAccessGranted()
        } else {
            // Access denied
            showAccessDenied()
            accessLogRepository.insertLog("ACCESS DENIED - $tagId")
            audioPlayer.playAccessDenied()
        }
    }

    private fun showAccessGranted(apartmentCode: String) {
        with(binding) {
            // Hide scanning elements
            scanContainer.visibility = View.GONE
            tvScanInstructions.visibility = View.GONE
            tvScanHint.visibility = View.GONE

            // Set up backgrounds
            accessResultContainer.background = ContextCompat.getDrawable(
                this@RFIDScannerActivity,
                R.drawable.access_granted_bg
            )

            // Set up icon
            cardResultIcon.setCardBackgroundColor(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.success_dark)
            )

            // Use a checkmark icon
            ivAccessIcon.setImageResource(R.drawable.ic_checkmark)
            ivAccessIcon.setColorFilter(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.white),
                PorterDuff.Mode.SRC_IN
            )

            // Set text
            tvAccessResult.text = getString(R.string.access_granted)
            tvAccessResult.setTextColor(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.success_text)
            )

            tvApartmentCode.text = getString(R.string.apartment_code, apartmentCode)

            // Show results
            cardAccessResult.visibility = View.VISIBLE
            btnReturn.visibility = View.VISIBLE
        }
    }

    private fun showAccessDenied() {
        with(binding) {
            // Hide scanning elements
            scanContainer.visibility = View.GONE
            tvScanInstructions.visibility = View.GONE
            tvScanHint.visibility = View.GONE

            // Set up backgrounds
            accessResultContainer.background = ContextCompat.getDrawable(
                this@RFIDScannerActivity,
                R.drawable.access_denied_bg
            )

            // Set up icon
            cardResultIcon.setCardBackgroundColor(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.error_dark)
            )

            // Use an X icon
            ivAccessIcon.setImageResource(android.R.drawable.ic_delete)
            ivAccessIcon.setColorFilter(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.white),
                PorterDuff.Mode.SRC_IN
            )

            // Set text
            tvAccessResult.text = getString(R.string.access_denied)
            tvAccessResult.setTextColor(
                ContextCompat.getColor(this@RFIDScannerActivity, R.color.error_text)
            )

            tvApartmentCode.text = getString(R.string.unauthorized_tag)

            // Show results
            cardAccessResult.visibility = View.VISIBLE
            btnReturn.visibility = View.VISIBLE
        }
    }
}