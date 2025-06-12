package com.example.trimly.ui.admin.masters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trimly.R
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.data.MasterDao
import com.example.trimly.data.EstablishmentDao
import com.example.trimly.data.Master
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog

class ManageMastersFragment : Fragment() {
    private lateinit var masterDao: MasterDao
    private lateinit var establishmentDao: EstablishmentDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MastersAdapter
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
        toolbar?.setTitle("Управління майстрами")
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        masterDao = MasterDao(requireContext())
        establishmentDao = EstablishmentDao(requireContext())
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MastersAdapter(
            onEditClick = { master ->
                showMasterDialog(master)
            },
            onDeleteClick = { master ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Підтвердження видалення")
                    .setMessage("Ви дійсно бажаєте видалити майстра ${master.firstName}?")
                    .setPositiveButton("Так") { _, _ ->
                        establishmentId?.let { estId ->
                            masterDao.deleteMaster(master.userid, estId)
                            loadMasters()
                            Toast.makeText(requireContext(), "Майстра видалено", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Ні", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            showMasterDialog(null)
        }

        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getInt("userid", -1)
        establishmentId = establishmentDao.getEstablishmentIdForAdmin(userId)
        if (establishmentId != null) {
            loadMasters()
        } else {
            Toast.makeText(requireContext(), "Ви не є адміном жодного закладу!", Toast.LENGTH_LONG).show()
        }
    }

    private fun showMasterDialog(master: Master?) {
        establishmentId?.let { id ->
            val dialog = MasterDialog(id) {
                loadMasters()
            }
            dialog.show(childFragmentManager, "master_dialog")
        }
    }

    private fun loadMasters() {
        val masters = masterDao.getMastersByEstablishment(establishmentId!!)
        adapter.updateItems(masters)
    }
}
