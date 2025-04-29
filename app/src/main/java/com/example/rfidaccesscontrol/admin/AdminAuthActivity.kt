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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.databinding.ActivityAdminAuthBinding
import java.time.Instant
import javax.crypto.spec.SecretKeySpec
import java.time.Duration

class AdminAuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminAuthBinding

    // Hardcoded TOTP secret key - in a real app, this should be stored more securely
    private val TOTP_SECRET = "YOUR_NEW_SECURE_KEY"  // Base32 encoded secret

    // TOTP generator
    private val totpGenerator = TimeBasedOneTimePasswordGenerator(
        Duration.ofSeconds(30),
        6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPinCodeFields()
        setupClickListeners()
    }

    private fun setupPinCodeFields() {
        val editTexts = arrayOf(
            binding.etCode1, binding.etCode2, binding.etCode3,
            binding.etCode4, binding.etCode5, binding.etCode6
        )

        // Set up auto-focus to next field
        for (i in 0 until editTexts.size - 1) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
            })
        }

        // Handle backspace to go to previous field
        for (i in 1 until editTexts.size) {
            editTexts[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editTexts[i].text.isEmpty()) {
                    editTexts[i - 1].requestFocus()
                    editTexts[i - 1].text.clear()
                    true
                } else {
                    false
                }
            }
        }

        // Handle "Done" action on the last field to close keyboard
        editTexts[5].setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTexts[5].windowToken, 0)
                true
            } else {
                false
            }
        }

        // Set initial focus
        editTexts[0].requestFocus()
    }

    private fun getEnteredCode(): String {
        return binding.etCode1.text.toString() +
                binding.etCode2.text.toString() +
                binding.etCode3.text.toString() +
                binding.etCode4.text.toString() +
                binding.etCode5.text.toString() +
                binding.etCode6.text.toString()
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnEnter.setOnClickListener {
            verifyTOTPCode()
        }

        // Hide error when user starts editing
        val editTexts = arrayOf(
            binding.etCode1, binding.etCode2, binding.etCode3,
            binding.etCode4, binding.etCode5, binding.etCode6
        )

        for (editText in editTexts) {
            editText.setOnClickListener {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    private fun verifyTOTPCode() {
        val enteredCode = getEnteredCode()

        try {
            // Decode Base32 secret key
            val decodedKey = base32Decode(TOTP_SECRET)
            val key = SecretKeySpec(decodedKey, "HmacSHA1")

            // Generate the current TOTP code
            val now = Instant.now()
            val currentCode = totpGenerator.generateOneTimePasswordString(key, now)

            // Compare with entered code
            if (enteredCode == currentCode) {
                // Code is correct, navigate to admin dashboard
                val intent = Intent(this, AdminDashboardActivity::class.java)
                startActivity(intent)
                finish() // Close the auth screen
            } else {
                // Code is incorrect, show error
                binding.tvError.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            // Handle any errors
            binding.tvError.visibility = View.VISIBLE
            e.printStackTrace()
        }
    }

    // Base32 decoder function
    private fun base32Decode(base32: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleanedInput = base32.uppercase().replace("[^A-Z2-7]".toRegex(), "")

        var bits = 0
        var value = 0
        val result = mutableListOf<Byte>()

        for (c in cleanedInput) {
            val charValue = alphabet.indexOf(c)
            if (charValue < 0) continue

            value = (value shl 5) or charValue
            bits += 5

            if (bits >= 8) {
                bits -= 8
                result.add(((value shr bits) and 0xFF).toByte())
            }
        }

        return result.toByteArray()
    }
}