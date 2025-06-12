package com.example.trimly.ui.admin.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.DetailedBooking

class BookingsAdapter(
    private var items: List<DetailedBooking>,
    private val onEditClick: (DetailedBooking) -> Unit,
    private val onDeleteClick: (DetailedBooking) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    fun updateItems(newItems: List<DetailedBooking>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvSalonName: TextView = view.findViewById(R.id.tvSalonName)
        private val tvServiceName: TextView = view.findViewById(R.id.tvServiceName)
        private val tvMasterName: TextView = view.findViewById(R.id.tvMasterName)
        private val tvDate: TextView = view.findViewById(R.id.tvDate)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)
        private val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        private val btnCancel: android.widget.Button = view.findViewById(R.id.btnCancel)

        fun bind(item: DetailedBooking) {
            tvSalonName.text = item.salonName
            tvServiceName.text = item.serviceName
            tvMasterName.text = item.masterName
            tvDate.text = item.date
            tvTime.text = "${item.startTime}-${item.endTime}"
            tvStatus.text = when (item.status) {
                com.example.trimly.data.BookingStatus.CONFIRMED, com.example.trimly.data.BookingStatus.PENDING -> "Заплановано"
                com.example.trimly.data.BookingStatus.CANCELLED -> "Скасовано"
                com.example.trimly.data.BookingStatus.COMPLETED -> "Завершено"
            }
            btnCancel.visibility = android.view.View.VISIBLE
            btnCancel.setOnClickListener { onDeleteClick(item) }
        }
    }
} 