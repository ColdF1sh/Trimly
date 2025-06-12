package com.example.trimly.ui.admin.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.ServiceDao
import com.example.trimly.data.Service
import com.example.trimly.data.EstablishmentDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.trimly.ui.admin.masters.ServiceDialog
import com.example.trimly.ui.admin.masters.ServicesAdapter

class ManageServicesFragment : Fragment() {
    private lateinit var serviceDao: ServiceDao
    private lateinit var establishmentDao: EstablishmentDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServicesAdapter
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
        val toolbar = view.findViewById<Toolbar?>(R.id.toolbar)
        toolbar?.setTitle("Управління послугами")
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        serviceDao = ServiceDao(requireContext())
        establishmentDao = EstablishmentDao(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ServicesAdapter(listOf(),
            onEditClick = { service ->
                establishmentId?.let { estId ->
                    val dialog = ServiceDialog(estId) {
                        loadServices()
                    }
                    // TODO: Передати дані для редагування (реалізувати у ServiceDialog)
                    dialog.show(childFragmentManager, "service_dialog")
                }
            },
            onDeleteClick = { service ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Підтвердження видалення")
                    .setMessage("Ви дійсно бажаєте видалити послугу ${service.name}?")
                    .setPositiveButton("Так") { _, _ ->
                        serviceDao.deleteService(service.serviceId)
                        loadServices()
                        Toast.makeText(requireContext(), "Послугу видалено", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Ні", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            establishmentId?.let { estId ->
                val dialog = ServiceDialog(estId) {
                    loadServices()
                }
                dialog.show(childFragmentManager, "service_dialog")
            } ?: Toast.makeText(requireContext(), "Не знайдено заклад!", Toast.LENGTH_SHORT).show()
        }

        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getInt("userid", -1)
        establishmentId = establishmentDao.getEstablishmentIdForAdmin(userId)
        if (establishmentId != null) {
            loadServices()
        } else {
            Toast.makeText(requireContext(), "Ви не є адміном жодного закладу!", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadServices() {
        val estId = establishmentId ?: return
        val services = serviceDao.getServicesByEstablishment(estId)
        adapter.updateItems(services)
    }
} 