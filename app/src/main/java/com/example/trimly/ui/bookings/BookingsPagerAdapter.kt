package com.example.trimly.ui.bookings

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import java.text.SimpleDateFormat
import java.util.*
import com.example.trimly.data.DetailedBooking
import com.example.trimly.data.BookingStatus

class BookingsAdapter(
    private val context: Context,
    private var bookings: MutableList<DetailedBooking>,
    private val onClick: (DetailedBooking) -> Unit,
    private val showCancelButton: Boolean,
    private val onStatusChange: (Int, BookingStatus) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val detailedBooking = bookings[position]
        holder.bind(detailedBooking, showCancelButton, onStatusChange)
    }

    override fun getItemCount() = bookings.size

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvService: TextView = itemView.findViewById(R.id.tvServiceName)
        private val tvMaster: TextView = itemView.findViewById(R.id.tvMasterName)
        private val statusView: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
        private val tvExpiredMessage: TextView = itemView.findViewById(R.id.tvExpiredMessage)
        private val tvSalonName: TextView = itemView.findViewById(R.id.tvSalonName)

        fun bind(detailedBooking: DetailedBooking, showCancelButton: Boolean, onStatusChange: (Int, BookingStatus) -> Unit) {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            tvDate.text = detailedBooking.date
            tvTime.text = detailedBooking.startTime
            tvService.text = detailedBooking.serviceName
            tvMaster.text = detailedBooking.masterName
            tvSalonName.text = detailedBooking.salonName

            when (detailedBooking.status) {
                BookingStatus.PENDING -> {
                    statusView.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                    tvExpiredMessage.visibility = View.GONE
                }
                BookingStatus.CONFIRMED -> {
                    statusView.text = "Заплановано"
                    statusView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark))
                    statusView.visibility = View.VISIBLE
                    btnCancel.visibility = if (showCancelButton) View.VISIBLE else View.GONE
                    tvExpiredMessage.visibility = View.GONE
                }
                BookingStatus.CANCELLED -> {
                    statusView.text = "Скасовано"
                    statusView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                    statusView.visibility = View.VISIBLE
                    btnCancel.visibility = View.GONE
                    tvExpiredMessage.visibility = View.GONE
                }
                BookingStatus.COMPLETED -> {
                    statusView.visibility = View.GONE
                    btnCancel.visibility = View.GONE
                    tvExpiredMessage.text = "Строк запису минув"
                    tvExpiredMessage.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    tvExpiredMessage.visibility = View.VISIBLE
                }
            }

            btnCancel.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))

            if (showCancelButton && detailedBooking.status == BookingStatus.CONFIRMED) {
                btnCancel.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Відмінити запис?")
                        .setMessage("Ви дійсно хочете відмінити цей запис?")
                        .setPositiveButton("Так") { _, _ ->
                            onStatusChange(detailedBooking.id, BookingStatus.CANCELLED)
                        }
                        .setNegativeButton("Ні", null)
                        .show()
                }
            } else {
                btnCancel.visibility = View.GONE
            }

            itemView.setOnClickListener { onClick(detailedBooking) }
        }
    }
} 