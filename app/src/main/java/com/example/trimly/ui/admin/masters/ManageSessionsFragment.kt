package com.example.trimly.ui.admin.masters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.MasterDao
import com.example.trimly.data.EstablishmentDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import android.widget.Toast

class ManageSessionsFragment : Fragment() {
    private lateinit var masterDao: MasterDao
    private lateinit var establishmentDao: EstablishmentDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SessionsGroupedAdapter
    private var establishmentId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setTitle("Управління записами")
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        masterDao = MasterDao(requireContext())
        establishmentDao = EstablishmentDao(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SessionsGroupedAdapter(emptyMap(), emptyMap())
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
            SessionDialog(masters) { masterId, startTime, endTime, dates ->
                val conflicts = masterDao.insertSessionsForDays(masterId, estId, startTime, endTime, dates)
                loadSessions()
                if (conflicts.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Деякі сеанси не додано: час зайнятий на дати: ${conflicts.joinToString(", ")}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Сеанси додано", Toast.LENGTH_SHORT).show()
                }
            }.show(childFragmentManager, "session_dialog")
        }

        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getInt("userid", -1)
        establishmentId = establishmentDao.getEstablishmentIdForAdmin(userId)
        if (establishmentId != null) {
            loadSessions()
        } else {
            Toast.makeText(requireContext(), "Ви не є адміном жодного закладу!", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadSessions() {
        val estId = establishmentId ?: return
        val sessions = masterDao.getAllSessionsByEstablishment(estId)
        val masters = masterDao.getMastersByEstablishment(estId)
        val masterMap = masters.associateBy { it.userid }
        val grouped = sessions.groupBy { it.date }
        adapter.updateItems(grouped)
        adapter.updateMasterMap(masterMap)
    }

    private fun getCurrentMasterId(): Int? {
        // TODO: реалізувати вибір майстра (наприклад, через окремий діалог або збереження)
        // Поки що повертає null, щоб не додавати сесії без майстра
        return null
    }
} 