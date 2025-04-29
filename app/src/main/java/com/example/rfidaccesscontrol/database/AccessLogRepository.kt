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
package com.example.rfidaccesscontrol.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.rfidaccesscontrol.models.AccessLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AccessLogRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun insertLog(message: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_LOG_TIMESTAMP, dateFormat.format(Date()))
            put(DatabaseHelper.COLUMN_LOG_MESSAGE, message)
        }

        return db.insert(DatabaseHelper.TABLE_ACCESS_LOG, null, values)
    }

    fun getAllLogs(): List<AccessLog> {
        val logs = mutableListOf<AccessLog>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_ACCESS_LOG,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_LOG_TIMESTAMP} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                logs.add(createLogFromCursor(it))
            }
        }

        return logs
    }

    private fun createLogFromCursor(cursor: Cursor): AccessLog {
        val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOG_ID)
        val timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOG_TIMESTAMP)
        val messageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOG_MESSAGE)

        return AccessLog(
            id = cursor.getLong(idIndex),
            timestamp = cursor.getString(timestampIndex),
            message = cursor.getString(messageIndex)
        )
    }
}