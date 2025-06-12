package com.example.trimly.ui.admin.masters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.trimly.R
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.example.trimly.data.Master
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.app.AlertDialog
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SessionDialog(
    private val masters: List<Master>,
    private val onAddSessions: (masterId: Int, startTime: String, endTime: String, dates: List<String>) -> Unit
) : DialogFragment() {

    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var dayCheckboxes: List<CheckBox>
    private lateinit var spinnerMaster: Spinner
    private var selectedMasterId: Int? = null
    private lateinit var etStartHour: EditText
    private lateinit var etStartMinute: EditText
    private lateinit var etEndHour: EditText
    private lateinit var etEndMinute: EditText
    private lateinit var btnPrevWeek: ImageButton
    private lateinit var btnNextWeek: ImageButton
    private lateinit var tvWeekRange: TextView
    private var mondayDate: Calendar = getMondayOfCurrentWeek()
    private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    private val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")

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
        return inflater.inflate(R.layout.dialog_add_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAdd = view.findViewById(R.id.btnAdd)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek)
        btnNextWeek = view.findViewById(R.id.btnNextWeek)
        tvWeekRange = view.findViewById(R.id.tvWeekRange)
        dayCheckboxes = listOf(
            view.findViewById(R.id.cbMonday),
            view.findViewById(R.id.cbTuesday),
            view.findViewById(R.id.cbWednesday),
            view.findViewById(R.id.cbThursday),
            view.findViewById(R.id.cbFriday),
            view.findViewById(R.id.cbSaturday),
            view.findViewById(R.id.cbSunday)
        )
        spinnerMaster = view.findViewById(R.id.spinnerMaster)
        etStartHour = view.findViewById(R.id.etStartHour)
        etStartMinute = view.findViewById(R.id.etStartMinute)
        etEndHour = view.findViewById(R.id.etEndHour)
        etEndMinute = view.findViewById(R.id.etEndMinute)

        val masterNames = masters.map { it.firstName + (it.lastName?.let { " $it" } ?: "") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, masterNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMaster.adapter = adapter
        spinnerMaster.setSelection(0)
        selectedMasterId = masters.getOrNull(0)?.userid
        spinnerMaster.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMasterId = masters[position].userid
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        updateWeekRange()
        btnPrevWeek.setOnClickListener {
            val cal = mondayDate.clone() as Calendar
            cal.add(Calendar.DAY_OF_MONTH, -7)
            val newMonday = cal.clone() as Calendar

            // Знайти понеділок поточного тижня
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            today.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            if (newMonday.before(today)) {
                Toast.makeText(requireContext(), "Не можна вибрати минулий тиждень", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mondayDate = cal
            updateWeekRange()
        }
        btnNextWeek.setOnClickListener {
            mondayDate.add(Calendar.DAY_OF_MONTH, 7)
            updateWeekRange()
        }

        btnAdd.setOnClickListener {
            val startHour = etStartHour.text.toString()
            val startMinute = etStartMinute.text.toString()
            val endHour = etEndHour.text.toString()
            val endMinute = etEndMinute.text.toString()
            val days = dayCheckboxes.mapIndexedNotNull { idx, cb -> if (cb.isChecked) idx else null }
            val masterId = selectedMasterId
            if (startHour.isBlank() || startMinute.isBlank() || endHour.isBlank() || endMinute.isBlank() || days.isEmpty() || masterId == null) {
                Toast.makeText(requireContext(), "Заповніть всі поля часу!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val sh = startHour.toIntOrNull()
            val sm = startMinute.toIntOrNull()
            val eh = endHour.toIntOrNull()
            val em = endMinute.toIntOrNull()
            if (sh == null || sm == null || eh == null || em == null || sh !in 0..23 || eh !in 0..23 || sm !in 0..59 || em !in 0..59) {
                Toast.makeText(requireContext(), "Години: 0-23, хвилини: 0-59", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val startTime = String.format(Locale.getDefault(), "%02d:%02d", sh, sm)
            val endTime = String.format(Locale.getDefault(), "%02d:%02d", eh, em)
            val selectedDates = days.map { dayIdx ->
                val cal = mondayDate.clone() as Calendar
                cal.add(Calendar.DAY_OF_MONTH, dayIdx)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
            }
            onAddSessions(masterId, startTime, endTime, selectedDates)
            dismiss()
        }
        btnCancel.setOnClickListener { dismiss() }

        // --- Автоперехід між полями часу ---
        etStartHour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2) etStartMinute.requestFocus()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        etStartMinute.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2) etEndHour.requestFocus()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        etEndHour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2) etEndMinute.requestFocus()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        etEndMinute.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2) {
                    etEndMinute.clearFocus()
                    val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(etEndMinute.windowToken, 0)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun updateWeekRange() {
        val monday = mondayDate.clone() as Calendar
        val sunday = monday.clone() as Calendar
        sunday.add(Calendar.DAY_OF_MONTH, 6)
        val text = "Пн, ${dateFormat.format(monday.time)} - Вс, ${dateFormat.format(sunday.time)}"
        tvWeekRange.text = text
    }

    private fun getMondayOfCurrentWeek(): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return cal
    }
} 