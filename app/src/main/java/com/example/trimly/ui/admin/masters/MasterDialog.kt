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
import com.example.trimly.data.Master
import com.example.trimly.data.MasterDao
import com.example.trimly.data.User
import com.example.trimly.data.UserDao
import timber.log.Timber

class MasterDialog(
    private val establishmentId: Int,
    private val onMasterSaved: () -> Unit
) : DialogFragment() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSpecialization: EditText
    private lateinit var etPortfolioUrl: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var userDao: UserDao
    private lateinit var masterDao: MasterDao

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
        return inflater.inflate(R.layout.dialog_master, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = UserDao(requireContext())
        masterDao = MasterDao(requireContext())

        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etPhone = view.findViewById(R.id.etPhone)
        etEmail = view.findViewById(R.id.etEmail)
        etSpecialization = view.findViewById(R.id.etSpecialization)
        etPortfolioUrl = view.findViewById(R.id.etPortfolioUrl)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnSave.setOnClickListener {
            saveMaster()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveMaster() {
        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val phone = etPhone.text.toString()
        val email = etEmail.text.toString()
        val specialization = etSpecialization.text.toString()
        val portfolioUrl = etPortfolioUrl.text.toString()

        if (firstName.isBlank() || phone.isBlank() || email.isBlank()) {
            // Show error message
            return
        }

        try {
            // First, create the user
            val user = User(
                userid = 0, // Will be set by the database
                firstName = firstName,
                lastName = if (lastName.isBlank()) null else lastName,
                email = email,
                phone = phone,
                role = "master",
                rating = 5.0,
                createdAt = null
            )

            // Insert the user and get the new user ID
            val userId = userDao.insertUser(user)
            if (userId == -1L) {
                Timber.e("Failed to insert user")
                return
            }

            // Create and insert the master
            val master = Master(
                userid = userId.toInt(),
                firstName = firstName,
                lastName = if (lastName.isBlank()) null else lastName,
                phone = phone,
                email = email,
                specialization = if (specialization.isBlank()) null else specialization,
                portfolioUrl = if (portfolioUrl.isBlank()) null else portfolioUrl,
                rating = 5.0,
                establishmentId = establishmentId
            )

            val masterId = masterDao.insertMaster(master)
            if (masterId == -1L) {
                Timber.e("Failed to insert master")
                return
            }

            onMasterSaved()
            dismiss()
        } catch (e: Exception) {
            Timber.e(e, "Error saving master")
        }
    }
} 