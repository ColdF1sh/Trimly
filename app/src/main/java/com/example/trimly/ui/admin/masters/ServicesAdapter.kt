package com.example.trimly.ui.admin.masters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.Service

class ServicesAdapter(
    private var items: List<Service>,
    private val onEditClick: (Service) -> Unit,
    private val onDeleteClick: (Service) -> Unit
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    fun updateItems(newItems: List<Service>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvServiceName)
        private val tvDescription: TextView = view.findViewById(R.id.tvServiceDescription)
        private val tvPrice: TextView = view.findViewById(R.id.tvServicePrice)
        private val tvDuration: TextView = view.findViewById(R.id.tvServiceDuration)
        private val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

        fun bind(item: Service) {
            tvName.text = item.name
            tvDescription.text = item.description ?: ""
            tvPrice.text = "${item.price} грн"
            tvDuration.text = "${item.duration} хв"
            btnEdit.setOnClickListener { onEditClick(item) }
            btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }
} 