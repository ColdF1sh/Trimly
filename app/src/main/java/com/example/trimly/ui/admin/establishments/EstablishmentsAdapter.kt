package com.example.trimly.ui.admin.establishments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.trimly.R
import com.example.trimly.ui.home.Salon
import com.example.trimly.ui.admin.base.BaseAdapter

class EstablishmentsAdapter(
    private val onEditClick: (Salon) -> Unit,
    private val onDeleteClick: (Salon) -> Unit
) : BaseAdapter<Salon>() {

    override fun getItemLayoutId() = R.layout.item_establishment

    override fun createViewHolder(view: View) = EstablishmentViewHolder(view)

    inner class EstablishmentViewHolder(view: View) : ViewHolder<Salon>(view) {
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        private val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        override fun bind(item: Salon) {
            tvName.text = item.name
            tvAddress.text = item.address

            btnEdit.setOnClickListener { onEditClick(item) }
            btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }
} 