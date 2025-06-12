package com.example.trimly.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trimly.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import java.io.Serializable
import java.util.ArrayList
import com.example.trimly.data.DetailedBooking
import com.example.trimly.data.BookingStatus

class BookingsListFragment : Fragment() {
    private var isUpcoming = true
    private lateinit var bookings: MutableList<DetailedBooking>
    lateinit var adapter: BookingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUpcoming = requireArguments().getBoolean(ARG_UPCOMING)
        @Suppress("DEPRECATION")
        bookings = (requireArguments().getSerializable(ARG_LIST) as? ArrayList<DetailedBooking>) ?: ArrayList()
    }

    fun updateBookings(newBookings: MutableList<DetailedBooking>) {
        bookings.clear()
        bookings.addAll(newBookings)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        val currentView = view
        if (recyclerView != null && currentView != null) {
            updateEmptyState(currentView, recyclerView)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty_bookings, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = BookingsAdapter(
            context = requireContext(),
            bookings = bookings,
            onClick = { _ -> /* Логіка при натисканні на запис, якщо потрібна */ },
            showCancelButton = isUpcoming,
            onStatusChange = { bookingId, status ->
                (parentFragment as? BookingsFragment)?.updateBookingStatus(bookingId, status)
            }
        )
        recyclerView.adapter = adapter

        updateEmptyState(view, recyclerView)

        return view
    }

    private fun updateEmptyState(view: View, recyclerView: RecyclerView) {
        val emptyTextView = view.findViewById<TextView>(R.id.emptyText)
        val hasItems = adapter.itemCount > 0

        recyclerView.visibility = if (hasItems) View.VISIBLE else View.GONE
        emptyTextView.visibility = if (hasItems) View.GONE else View.VISIBLE

        if (!hasItems) {
            emptyTextView.text = if (isUpcoming) {
                getString(R.string.empty_upcoming)
            } else {
                getString(R.string.empty_history)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val ARG_UPCOMING = "upcoming"
        private const val ARG_LIST = "list"
        fun newInstance(isUpcoming: Boolean, allBookings: MutableList<DetailedBooking>): BookingsListFragment {
            val fragment = BookingsListFragment()
            val args = Bundle()
            args.putBoolean(ARG_UPCOMING, isUpcoming)
            args.putSerializable(ARG_LIST, ArrayList(allBookings))
            fragment.arguments = args
            return fragment
        }
    }
} 