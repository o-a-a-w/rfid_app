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
package com.example.rfidaccesscontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidaccesscontrol.R
import com.example.rfidaccesscontrol.models.Tag

class TagListAdapter(
    private val tags: List<Tag>,
    private val onDeleteClick: ((Tag) -> Unit)? = null
) : RecyclerView.Adapter<TagListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTagId: TextView = view.findViewById(R.id.tvTagId)
        val tvApartment: TextView = view.findViewById(R.id.tvApartment)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnDelete: View = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]

        // Mask part of the tag ID for security
        val maskedTagId = if (tag.tagId.length > 4) {
            tag.tagId.substring(0, 4) + "***"
        } else {
            tag.tagId
        }

        holder.tvTagId.text = maskedTagId
        holder.tvApartment.text = tag.apartmentCode
        holder.tvDate.text = tag.creationDate

        // Show or hide delete button based on whether a listener is provided
        if (onDeleteClick != null) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                onDeleteClick.invoke(tag)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = tags.size
}