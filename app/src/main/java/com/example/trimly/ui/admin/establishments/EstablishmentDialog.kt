package com.example.trimly.ui.admin.establishments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.trimly.R
import com.example.trimly.data.EstablishmentDao
import com.example.trimly.ui.home.Salon
import com.example.trimly.ui.admin.base.BaseItemDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EstablishmentDialog : BaseItemDialog<Salon>() {

    private lateinit var establishmentDao: EstablishmentDao
    private var salon: Salon? = null

    private lateinit var tilName: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etPhone: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        establishmentDao = EstablishmentDao(requireContext())
        arguments?.let {
            salon = it.getSerializable(ARG_SALON) as? Salon
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilName = view.findViewById(R.id.tilName)
        tilAddress = view.findViewById(R.id.tilAddress)
        tilPhone = view.findViewById(R.id.tilPhone)
        etName = view.findViewById(R.id.etName)
        etAddress = view.findViewById(R.id.etAddress)
        etPhone = view.findViewById(R.id.etPhone)

        setupValidation()

        view.findViewById<TextView>(R.id.tvTitle).text = if (isEditMode()) {
            "Редагувати заклад"
        } else {
            "Додати заклад"
        }
    }

    override fun getLayoutId() = R.layout.dialog_establishment
    override fun getSaveButtonId() = R.id.btnSave
    override fun getCancelButtonId() = R.id.btnCancel

    override fun setupValidation() {
        etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateName()
        }
        etAddress.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateAddress()
        }
        etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePhone()
        }
    }

    override fun validateInputs(): Boolean {
        var isValid = true
        if (!validateName()) isValid = false
        if (!validateAddress()) isValid = false
        if (!validatePhone()) isValid = false
        return isValid
    }

    private fun validateName(): Boolean {
        val name = etName.text.toString().trim()
        return when {
            name.isEmpty() -> {
                showError(tilName, "Введіть назву закладу")
                false
            }
            else -> {
                clearError(tilName)
                true
            }
        }
    }

    private fun validateAddress(): Boolean {
        val address = etAddress.text.toString().trim()
        return when {
            address.isEmpty() -> {
                showError(tilAddress, "Введіть адресу закладу")
                false
            }
            else -> {
                clearError(tilAddress)
                true
            }
        }
    }

    private fun validatePhone(): Boolean {
        val phone = etPhone.text.toString().trim()
        return when {
            phone.isEmpty() -> {
                showError(tilPhone, "Введіть номер телефону")
                false
            }
            !phone.matches(Regex("^\\+?[0-9]{10,13}$")) -> {
                showError(tilPhone, "Невірний формат номеру")
                false
            }
            else -> {
                clearError(tilPhone)
                true
            }
        }
    }

    override fun saveItem() {
        val name = etName.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        // latitude, longitude не редагуються тут
        if (isEditMode()) {
            // Тут можна реалізувати оновлення, якщо потрібно
            (parentFragment as? ManageEstablishmentsFragment)?.showSnackbar("Оновлення закладу не реалізовано")
        } else {
            // Тут можна реалізувати додавання, якщо потрібно
            (parentFragment as? ManageEstablishmentsFragment)?.showSnackbar("Додавання закладу не реалізовано")
        }
        (parentFragment as? ManageEstablishmentsFragment)?.refreshData()
        dismiss()
    }

    override fun isEditMode() = salon != null

    override fun loadItemData() {
        salon?.let { est ->
            etName.setText(est.name)
            etAddress.setText(est.address)
            etPhone.setText(est.phone)
        }
    }

    companion object {
        private const val ARG_SALON = "salon"

        fun newInstance(salon: Salon): EstablishmentDialog {
            return EstablishmentDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SALON, salon)
                }
            }
        }
    }
} 