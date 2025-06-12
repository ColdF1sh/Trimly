package com.example.trimly.ui.admin.establishments

import android.os.Bundle
import android.view.View
import com.example.trimly.R
import com.example.trimly.data.EstablishmentDao
import com.example.trimly.ui.home.Salon
import com.example.trimly.ui.admin.base.BaseManageFragment
import android.widget.Toolbar

class ManageEstablishmentsFragment : BaseManageFragment<Salon>() {

    private lateinit var establishmentDao: EstablishmentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        establishmentDao = EstablishmentDao(requireContext())
    }

    override fun getLayoutId() = R.layout.fragment_manage_base
    override fun getRecyclerViewId() = R.id.recyclerView
    override fun getFabId() = R.id.fab

    override fun createAdapter() = EstablishmentsAdapter(
        onEditClick = { salon ->
            showEditDialog(salon)
        },
        onDeleteClick = { salon ->
            deleteEstablishment(salon)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Додаємо кнопку назад у Toolbar (або у верхню частину фрагмента)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar?>(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun loadData() {
        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userRole = prefs.getString("role", "client")
        val userId = prefs.getInt("userid", -1)
        val salons = if (userRole?.trim()?.lowercase() == "admin" && userId != -1) {
            establishmentDao.getEstablishmentsForAdmin(userId)
        } else {
            establishmentDao.getAllEstablishments()
        }
        adapter.updateItems(salons)
    }

    override fun showAddDialog() {
        EstablishmentDialog().show(childFragmentManager, "add_establishment")
    }

    private fun showEditDialog(salon: Salon) {
        EstablishmentDialog.newInstance(salon)
            .show(childFragmentManager, "edit_establishment")
    }

    private fun deleteEstablishment(salon: Salon) {
        // Тут потрібно реалізувати видалення через DAO, якщо потрібно
        showSnackbar("Заклад видалено (реалізуйте видалення у DAO)")
        refreshData()
    }
} 