package com.example.trimly.ui.admin.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.BookingDao
import com.example.trimly.data.EstablishmentDao
import com.example.trimly.data.DetailedBooking
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.trimly.data.MasterDao
import com.example.trimly.data.Master
import com.example.trimly.ui.admin.masters.SessionDialog

class ManageBookingsFragment : Fragment() {
    private lateinit var bookingDao: BookingDao
    private lateinit var establishmentDao: EstablishmentDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookingsAdapter
    private var establishmentId: Int? = null
    private lateinit var masterDao: MasterDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar?>(R.id.toolbar)
        toolbar?.setTitle("Управління записами")
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        bookingDao = BookingDao(requireContext())
        establishmentDao = EstablishmentDao(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = BookingsAdapter(listOf(),
            onEditClick = { booking ->
                // TODO: Додати діалог редагування запису
            },
            onDeleteClick = { booking ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Підтвердження видалення")
                    .setMessage("Ви дійсно бажаєте видалити запис для ${booking.clientName}?")
                    .setPositiveButton("Так") { _, _ ->
                        bookingDao.deleteBooking(booking.id)
                        Toast.makeText(requireContext(), "Запис видалено", Toast.LENGTH_SHORT).show()
                        loadBookings()
                    }
                    .setNegativeButton("Ні", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
        fab.setOnClickListener {
            val estId = establishmentId ?: return@setOnClickListener
            val masters = masterDao.getMastersByEstablishment(estId)
            if (masters.isEmpty()) {
                Toast.makeText(requireContext(), "Немає майстрів у цьому закладі!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SessionDialog(masters) { masterId, startTime, endTime, days ->
                masterDao.insertSessionsForDays(masterId, estId, startTime, endTime, days)
                loadBookings()
                Toast.makeText(requireContext(), "Сеанси додано", Toast.LENGTH_SHORT).show()
            }.show(childFragmentManager, "session_dialog")
        }

        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getInt("userid", -1)
        establishmentId = establishmentDao.getEstablishmentIdForAdmin(userId)
        if (establishmentId != null) {
            loadBookings()
        } else {
            Toast.makeText(requireContext(), "Ви не є адміном жодного закладу!", Toast.LENGTH_LONG).show()
        }

        masterDao = MasterDao(requireContext())
    }

    private fun loadBookings() {
        val bookings = bookingDao.getAllDetailedBookings()
        adapter.updateItems(bookings)
    }
} 