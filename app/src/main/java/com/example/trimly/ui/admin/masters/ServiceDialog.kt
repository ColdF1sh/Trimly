package com.example.trimly.ui.admin.masters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.trimly.R
import com.example.trimly.data.Service
import com.example.trimly.data.ServiceDao
import timber.log.Timber

class ServiceDialog(
    private val establishmentId: Int,
    private val onServiceSaved: () -> Unit
) : DialogFragment() {

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var serviceDao: ServiceDao

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceDao = ServiceDao(requireContext())

        etName = view.findViewById(R.id.etServiceName)
        etDescription = view.findViewById(R.id.etServiceDescription)
        etPrice = view.findViewById(R.id.etServicePrice)
        etDuration = view.findViewById(R.id.etServiceDuration)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            saveService()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveService() {
        val name = etName.text.toString()
        val description = etDescription.text.toString()
        val price = etPrice.text.toString().toDoubleOrNull()
        val duration = etDuration.text.toString().toIntOrNull()

        if (name.isBlank() || price == null || duration == null) {
            // Show error message
            return
        }

        try {
            val service = Service(
                serviceId = 0,
                establishmentId = establishmentId,
                name = name,
                description = if (description.isBlank()) null else description,
                price = price,
                duration = duration
            )
            val id = serviceDao.insertService(service)
            if (id == -1L) {
                Timber.e("Failed to insert service")
                return
            }
            onServiceSaved()
            dismiss()
        } catch (e: Exception) {
            Timber.e(e, "Error saving service")
        }
    }
} 