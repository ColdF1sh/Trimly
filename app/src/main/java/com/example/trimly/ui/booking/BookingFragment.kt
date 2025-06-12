package com.example.trimly.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.example.trimly.ui.home.Salon
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.trimly.data.MasterDao
import com.example.trimly.data.ServiceDao
import com.example.trimly.data.BookingDao
import com.example.trimly.data.MasterSession
import com.example.trimly.data.Master
import com.example.trimly.data.Service
import com.example.trimly.data.BookingStatus
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Locale

class BookingFragment : Fragment() {

    private val args: BookingFragmentArgs by navArgs()

    private lateinit var masterDao: MasterDao
    private lateinit var serviceDao: ServiceDao
    private lateinit var bookingDao: BookingDao

    private lateinit var btnCreateBooking: Button
    private lateinit var masterAutoCompleteTextView: AutoCompleteTextView
    private lateinit var serviceAutoCompleteTextView: AutoCompleteTextView
    private lateinit var sessionAutoCompleteTextView: AutoCompleteTextView

    private lateinit var toolbar: Toolbar

    private var availableSessions: List<MasterSession> = listOf()
    private var allMasters: List<Master> = listOf()
    private var allServices: List<Service> = listOf()

    private var selectedMaster: Master? = null
    private var selectedService: Service? = null
    private var selectedSession: MasterSession? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        masterDao = MasterDao(requireContext())
        serviceDao = ServiceDao(requireContext())
        bookingDao = BookingDao(requireContext())

        toolbar = view.findViewById(R.id.toolbarBooking)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val salon = args.salon
        view.findViewById<TextView>(R.id.tvSalonName).text = salon.name

        masterAutoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewMaster)
        serviceAutoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewService)
        sessionAutoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewSession)

        btnCreateBooking = view.findViewById(R.id.btnCreateBooking)

        // Load masters and services
        allMasters = masterDao.getMastersByEstablishment(salon.establishmentId)
        val masterNames = allMasters.map { it.firstName + (if (!it.lastName.isNullOrBlank()) " " + it.lastName else "") }
        val masterAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, masterNames)
        masterAutoCompleteTextView.setAdapter(masterAdapter)

        allServices = serviceDao.getServicesByEstablishment(salon.establishmentId)
        val serviceNames = allServices.map { it.name }
        val serviceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, serviceNames)
        serviceAutoCompleteTextView.setAdapter(serviceAdapter)

        // Set up listener for master selection
        masterAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedMasterName = parent.getItemAtPosition(position).toString()
            selectedMaster = allMasters.find { it.firstName + (if (!it.lastName.isNullOrBlank()) " " + it.lastName else "") == selectedMasterName }
            selectedSession = null
            sessionAutoCompleteTextView.setText("")
            availableSessions = listOf()
            updateSessionDropdown()

            selectedMaster?.let {
                availableSessions = masterDao.getAvailableMasterSessions(it.userid, salon.establishmentId)
                updateSessionDropdown()
            }
        }

        serviceAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val selectedServiceName = parent.getItemAtPosition(position).toString()
            selectedService = allServices.find { it.name == selectedServiceName }
        }

        sessionAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val selectedSessionString = parent.getItemAtPosition(position).toString()

            selectedSession = availableSessions.find { session ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                try {
                    val sessionDate = dateFormat.parse(session.date) ?: return@find false
                    val sessionStartTime = timeFormat.parse(session.startTime) ?: return@find false
                    val sessionEndTime = timeFormat.parse(session.endTime) ?: return@find false

                    val sessionString = "${dateFormat.format(sessionDate)} ${timeFormat.format(sessionStartTime)}-${timeFormat.format(sessionEndTime)}"
                    sessionString == selectedSessionString
                } catch (e: Exception) {
                    false
                }
            }
        }

        btnCreateBooking.setOnClickListener {
            if (selectedMaster == null || selectedService == null || selectedSession == null) {
                Toast.makeText(requireContext(), "Будь ласка, оберіть майстра, послугу та доступну сесію.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
            val userid = prefs.getInt("userid", -1)
            if (userid == -1) {
                Toast.makeText(requireContext(), "Користувач не авторизований", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newRowId = bookingDao.addBooking(
                clientId = userid,
                sessionId = selectedSession!!.sessionId,
                serviceId = selectedService!!.serviceId,
                establishmentId = salon.establishmentId
            )

            if (newRowId != -1L) {
                Toast.makeText(requireContext(), "Запис успішно створено!", Toast.LENGTH_SHORT).show()
                // Ставимо флаг для оновлення записів
                val prefs = requireContext().getSharedPreferences("booking_update", android.content.Context.MODE_PRIVATE)
                prefs.edit().putBoolean("should_update_bookings", true).apply()
                findNavController().popBackStack(R.id.navigation_home, false)
                (requireActivity() as? AppCompatActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.navigation_bookings
            } else {
                Toast.makeText(requireContext(), "Помилка при створенні запису.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Оновлюємо список сесій, якщо був створений новий запис
        val prefs = requireContext().getSharedPreferences("booking_update", android.content.Context.MODE_PRIVATE)
        if (prefs.getBoolean("should_update_bookings", false)) {
            selectedMaster?.let {
                availableSessions = masterDao.getAvailableMasterSessions(it.userid, args.salon.establishmentId)
                updateSessionDropdown()
            }
            prefs.edit().putBoolean("should_update_bookings", false).apply()
        }
    }

    private fun updateSessionDropdown() {
        val sessionStrings = availableSessions.mapNotNull { session ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            try {
                val sessionDate = dateFormat.parse(session.date) ?: return@mapNotNull null
                val sessionStartTime = timeFormat.parse(session.startTime) ?: return@mapNotNull null
                val sessionEndTime = timeFormat.parse(session.endTime) ?: return@mapNotNull null

                "${dateFormat.format(sessionDate)} ${timeFormat.format(sessionStartTime)}-${timeFormat.format(sessionEndTime)}"
            } catch (e: Exception) {
                null
            }
        }
        val sessionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sessionStrings)
        sessionAutoCompleteTextView.setAdapter(sessionAdapter)
    }
} 