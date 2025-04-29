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
import android.database.sqlite.SQLiteDatabase
import com.example.rfidaccesscontrol.models.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TagsRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun insertTag(tagId: String, apartmentCode: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TAG_RFID, tagId)
            put(DatabaseHelper.COLUMN_TAG_APARTMENT, apartmentCode)
            put(DatabaseHelper.COLUMN_TAG_CREATION_DATE, dateFormat.format(Date()))
        }

        return db.insert(DatabaseHelper.TABLE_TAGS, null, values)
    }

    fun getAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TAGS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_TAG_CREATION_DATE} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                tags.add(createTagFromCursor(it))
            }
        }

        return tags
    }

    fun findTagById(tagId: String): Tag? {
        val db = dbHelper.readableDatabase
        val selection = "${DatabaseHelper.COLUMN_TAG_RFID} = ?"
        val selectionArgs = arrayOf(tagId)

        val cursor = db.query(
            DatabaseHelper.TABLE_TAGS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var tag: Tag? = null
        cursor.use {
            if (it.moveToFirst()) {
                tag = createTagFromCursor(it)
            }
        }

        return tag
    }

    fun deleteTag(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${DatabaseHelper.COLUMN_TAG_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        return db.delete(DatabaseHelper.TABLE_TAGS, selection, selectionArgs)
    }

    private fun createTagFromCursor(cursor: Cursor): Tag {
        val idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TAG_ID)
        val tagIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TAG_RFID)
        val apartmentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TAG_APARTMENT)
        val dateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TAG_CREATION_DATE)

        return Tag(
            id = cursor.getLong(idIndex),
            tagId = cursor.getString(tagIdIndex),
            apartmentCode = cursor.getString(apartmentIndex),
            creationDate = cursor.getString(dateIndex)
        )
    }
}