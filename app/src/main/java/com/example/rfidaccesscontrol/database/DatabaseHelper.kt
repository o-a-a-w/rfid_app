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

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "rfid_access.db"
        private const val DATABASE_VERSION = 1

        // Tags table
        const val TABLE_TAGS = "tags"
        const val COLUMN_TAG_ID = "id"
        const val COLUMN_TAG_RFID = "tag_id"
        const val COLUMN_TAG_APARTMENT = "apartment_code"
        const val COLUMN_TAG_CREATION_DATE = "creation_date"

        // Access log table
        const val TABLE_ACCESS_LOG = "access_log"
        const val COLUMN_LOG_ID = "id"
        const val COLUMN_LOG_TIMESTAMP = "timestamp"
        const val COLUMN_LOG_MESSAGE = "message"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Tags table
        val createTagsTable = """
            CREATE TABLE $TABLE_TAGS (
                $COLUMN_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TAG_RFID TEXT UNIQUE NOT NULL,
                $COLUMN_TAG_APARTMENT TEXT NOT NULL,
                $COLUMN_TAG_CREATION_DATE TEXT NOT NULL
            )
        """.trimIndent()

        // Create AccessLog table
        val createAccessLogTable = """
            CREATE TABLE $TABLE_ACCESS_LOG (
                $COLUMN_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOG_TIMESTAMP TEXT NOT NULL,
                $COLUMN_LOG_MESSAGE TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTagsTable)
        db.execSQL(createAccessLogTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCESS_LOG")
        onCreate(db)
    }
}