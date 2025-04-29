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

package com.example.rfidaccesscontrol.utils

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import java.io.IOException

object RFIDUtils {
    /**
     * Converts the NFC tag to a hex string
     */
    fun getTagId(tag: Tag?): String? {
        if (tag == null) return null

        return bytesToHexString(tag.id)
    }

    /**
     * Convert byte array to hex string
     */
    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString().uppercase()
    }

    /**
     * Check if the tag ID matches standard RFID format
     */
    fun isValidRFIDTag(tagId: String): Boolean {
        // Check if tag is a valid hexadecimal string of appropriate length
        return tagId.matches(Regex("[A-F0-9]{8,}"))
    }
}