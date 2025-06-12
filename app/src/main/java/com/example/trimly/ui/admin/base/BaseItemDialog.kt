package com.example.trimly.ui.admin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

abstract class BaseItemDialog<T> : BottomSheetDialogFragment() {

    protected lateinit var btnSave: MaterialButton
    protected lateinit var btnCancel: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSave = view.findViewById(getSaveButtonId())
        btnCancel = view.findViewById(getCancelButtonId())

        setupButtons()
        if (isEditMode()) {
            loadItemData()
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener {
            if (validateInputs()) {
                saveItem()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    protected fun showError(inputLayout: TextInputLayout, error: String) {
        inputLayout.error = error
    }

    protected fun clearError(inputLayout: TextInputLayout) {
        inputLayout.error = null
    }

    abstract fun getLayoutId(): Int
    abstract fun getSaveButtonId(): Int
    abstract fun getCancelButtonId(): Int
    abstract fun setupValidation()
    abstract fun validateInputs(): Boolean
    abstract fun saveItem()
    abstract fun isEditMode(): Boolean
    abstract fun loadItemData()
} 