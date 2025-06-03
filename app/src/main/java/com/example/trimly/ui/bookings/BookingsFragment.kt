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
import com.example.trimly.data.BookingDao
import android.util.Log
import java.util.*
import com.example.trimly.data.DetailedBooking
import com.example.trimly.data.BookingStatus

class BookingsFragment : Fragment() {

    private lateinit var bookingDao: BookingDao
    private var allBookings: MutableList<DetailedBooking> = mutableListOf()

    private var upcomingFragment: BookingsListFragment? = null
    private var historyFragment: BookingsListFragment? = null
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BookingsFragment", "onCreate called")
        bookingDao = BookingDao(requireContext())
        allBookings.addAll(bookingDao.getAllDetailedBookings())
        Log.d("BookingsFragment", "Detailed Bookings loaded from DB in onCreate: ${allBookings.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BookingsFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BookingsFragment", "onViewCreated called")
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment {
                Log.d("BookingsFragment", "createFragment called for position: $position")
                return when (position) {
                    0 -> {
                        Log.d("BookingsFragment", "Filtering for upcoming tab. allBookings size: ${allBookings.size}")
                        val fragment = BookingsListFragment.newInstance(
                            true, // isUpcoming = true
                            allBookings.filter { detailedBooking ->
                                detailedBooking.status == BookingStatus.PENDING || detailedBooking.status == BookingStatus.CONFIRMED
                            }.toMutableList()
                        )
                        upcomingFragment = fragment
                        fragment
                    }
                    1 -> {
                        Log.d("BookingsFragment", "Filtering for history tab. allBookings size: ${allBookings.size}")
                        val fragment = BookingsListFragment.newInstance(
                            false, // isUpcoming = false
                            allBookings.filter { detailedBooking ->
                                detailedBooking.status == BookingStatus.CANCELLED || detailedBooking.status == BookingStatus.COMPLETED
                            }.toMutableList()
                        )
                        historyFragment = fragment
                        fragment
                    }
                    else -> throw IllegalArgumentException()
                }
            }
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_upcoming)
                1 -> getString(R.string.tab_history)
                else -> ""
            }
        }.attach()
        val tabToOpen = arguments?.getInt("tabToOpen") ?: 0
        viewPager.currentItem = tabToOpen
    }

    override fun onResume() {
        super.onResume()
        notifyAllAdapters()
    }

    fun updateBookingStatus(bookingId: Int, status: BookingStatus) {
        val bookingToUpdate = allBookings.find { it.id == bookingId }
        bookingToUpdate?.let { booking ->
            val rowsAffected = bookingDao.updateAppointmentStatus(booking.id, status)
            Log.d("BookingDao", "Оновлено статус для Appointment id=${booking.id} до $status. Кількість оновлених рядків: $rowsAffected")
            if (status == BookingStatus.CANCELLED) {
                bookingDao.updateMasterSessionStatus(booking.sessionId, BookingStatus.PENDING)
            }
        notifyAllAdapters()
        } ?: run {
            Log.e("BookingsFragment", "Booking with id $bookingId not found for status update.")
        }
    }

    fun notifyAllAdapters() {
        Log.d("BookingsFragment", "notifyAllAdapters called")
        allBookings.clear()
        allBookings.addAll(bookingDao.getAllDetailedBookings())
        Log.d("BookingsFragment", "Detailed Bookings reloaded from DB in notifyAllAdapters: ${allBookings.size}")

        val currentUpcomingFragment = upcomingFragment
        Log.d("BookingsFragment", "Updating upcoming fragment bookings. allBookings size: ${allBookings.size}")
        currentUpcomingFragment?.updateBookings(allBookings.filter { detailedBooking ->
            detailedBooking.status == BookingStatus.PENDING || detailedBooking.status == BookingStatus.CONFIRMED
        }.toMutableList())

        val currentHistoryFragment = historyFragment
        Log.d("BookingsFragment", "Updating history fragment bookings. allBookings size: ${allBookings.size}")
        currentHistoryFragment?.updateBookings(allBookings.filter { detailedBooking ->
            detailedBooking.status == BookingStatus.CANCELLED || detailedBooking.status == BookingStatus.COMPLETED
        }.toMutableList())

        currentUpcomingFragment?.adapter?.notifyDataSetChanged()
        currentHistoryFragment?.adapter?.notifyDataSetChanged()
    }

    fun navigateToHistoryTab() {
        viewPager.currentItem = 1
    }
}

class EmptyBookingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty_bookings, container, false)
        val textView = view.findViewById<TextView>(R.id.emptyText)
        val msgRes = requireArguments().getInt(ARG_MSG_RES)
        textView.setText(msgRes)
        return view
    }
    companion object {
        private const val ARG_MSG_RES = "msg_res"
        fun newInstance(msgRes: Int): EmptyBookingsFragment {
            val fragment = EmptyBookingsFragment()
            val args = Bundle()
            args.putInt(ARG_MSG_RES, msgRes)
            fragment.arguments = args
            return fragment
        }
    }
} 