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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rfidaccesscontrol.R
import com.example.rfidaccesscontrol.models.AccessLog

class AccessLogAdapter(
    private val context: Context,
    private val logs: List<AccessLog>
) : RecyclerView.Adapter<AccessLogAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_access_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val log = logs[position]

        holder.tvTimestamp.text = log.timestamp
        holder.tvMessage.text = log.message

        // Set background color based on access result
        if (log.message.contains("GRANTED")) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.success_light)
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.error_light)
            )
        }
    }

    override fun getItemCount() = logs.size
}