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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rfidaccesscontrol.BaseActivity
import com.example.rfidaccesscontrol.R
import com.example.rfidaccesscontrol.adapters.TagListAdapter
import com.example.rfidaccesscontrol.database.TagsRepository
import com.example.rfidaccesscontrol.databinding.ActivityDeleteTagBinding
import com.example.rfidaccesscontrol.models.Tag

class DeleteTagActivity : BaseActivity() {
    private lateinit var binding: ActivityDeleteTagBinding
    private lateinit var tagsRepository: TagsRepository
    private lateinit var adapter: TagListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteTagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tagsRepository = TagsRepository(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        loadTags()
    }

    private fun loadTags() {
        val tags = tagsRepository.getAllTags()

        if (tags.isEmpty()) {
            binding.tvNoTags.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoTags.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            adapter = TagListAdapter(tags) { tag ->
                confirmDeleteTag(tag)
            }

            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@DeleteTagActivity)
                adapter = this@DeleteTagActivity.adapter
            }
        }
    }

    private fun confirmDeleteTag(tag: Tag) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_tag)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteTag(tag)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteTag(tag: Tag) {
        val result = tagsRepository.deleteTag(tag.id)

        if (result > 0) {
            Toast.makeText(this, getString(R.string.tag_deleted), Toast.LENGTH_SHORT).show()
            // Reload the tags
            loadTags()
        } else {
            Toast.makeText(this, getString(R.string.tag_error), Toast.LENGTH_SHORT).show()
        }
    }
}