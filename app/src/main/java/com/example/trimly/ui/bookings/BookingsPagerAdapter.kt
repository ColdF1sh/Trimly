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
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
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

    fun updateList(newList: List<DetailedBooking>) {
        bookings.clear()
        bookings.addAll(newList)
        notifyDataSetChanged()
    }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(detailedBooking: DetailedBooking, showCancelButton: Boolean, onStatusChange: (Int, BookingStatus) -> Unit) {
            itemView.findViewById<TextView>(R.id.tvSalonName).text = detailedBooking.salonName
            itemView.findViewById<TextView?>(R.id.tvMasterName)?.text = detailedBooking.masterName
            itemView.findViewById<TextView?>(R.id.tvServiceName)?.text = detailedBooking.serviceName

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(detailedBooking.date)
                val startTime = timeFormat.parse(detailedBooking.startTime)
                val endTime = timeFormat.parse(detailedBooking.endTime)

                itemView.findViewById<TextView>(R.id.tvDate).text = date?.let { dateFormat.format(it) }
                itemView.findViewById<TextView>(R.id.tvTime).text = "${timeFormat.format(startTime)}-${timeFormat.format(endTime)}"

            } catch (e: Exception) {
                Log.e("BookingsAdapter", "Error parsing date or time: ${e.message}")
                itemView.findViewById<TextView>(R.id.tvDate).text = detailedBooking.date
                itemView.findViewById<TextView>(R.id.tvTime).text = "${detailedBooking.startTime}-${detailedBooking.endTime}"
            }

            val statusView = itemView.findViewById<TextView>(R.id.tvStatus)
            val btnCancel = itemView.findViewById<Button>(R.id.btnCancel)
            val tvExpiredMessage = itemView.findViewById<TextView>(R.id.tvExpiredMessage)

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