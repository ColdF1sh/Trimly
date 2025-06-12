package com.example.trimly.ui.admin.masters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.Master

class MastersAdapter(
    private val onEditClick: (Master) -> Unit,
    private val onDeleteClick: (Master) -> Unit
) : RecyclerView.Adapter<MastersAdapter.MasterViewHolder>() {

    private var items: List<Master> = listOf()

    fun updateItems(newItems: List<Master>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_master, parent, false)
        return MasterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MasterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class MasterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        private val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        private val tvSpecialization: TextView = view.findViewById(R.id.tvSpecialization)
        private val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(item: Master) {
            tvName.text = "${item.firstName} ${item.lastName ?: ""}".trim()
            tvPhone.text = item.phone
            tvEmail.text = item.email
            tvSpecialization.text = item.specialization ?: ""
            btnEdit.setOnClickListener { onEditClick(item) }
            btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }
} 