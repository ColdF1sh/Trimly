package com.example.trimly.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.trimly.R
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController

class AdminPanelFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        // Додаємо Toolbar з кнопкою назад
        val toolbar = Toolbar(context).apply {
            title = "Панель управління"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            setTitleTextColor(ContextCompat.getColor(context, R.color.black))
            elevation = 8f
            minimumHeight = (resources.displayMetrics.density * 56).toInt() // 56dp стандартна висота
        }
        layout.addView(toolbar, 0)

        val title = Button(context).apply {
            text = "Адмін-панель закладу"
            isAllCaps = false
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 22f
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            isEnabled = false
        }
        layout.addView(title)

        val btnAddMaster = createAdminButton(context, "Додати майстра")
        val btnEditSchedules = createAdminButton(context, "Редагувати графіки майстрів")
        val btnEditEstablishment = createAdminButton(context, "Редагувати інформацію про заклад")
        val btnEditServices = createAdminButton(context, "Додати/редагувати послуги")
        val btnRemoveMaster = createAdminButton(context, "Видалити майстра із закладу")

        layout.addView(btnAddMaster)
        layout.addView(btnEditSchedules)
        layout.addView(btnEditEstablishment)
        layout.addView(btnEditServices)
        layout.addView(btnRemoveMaster)

        // TODO: Додати навігацію на відповідні екрани

        return layout
    }

    private fun createAdminButton(context: android.content.Context, text: String): Button {
        return Button(context).apply {
            this.text = text
            isAllCaps = false
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textSize = 18f
            setBackgroundColor(ContextCompat.getColor(context, R.color.teal_700))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 24, 0, 0)
            layoutParams = params
            elevation = 4f
        }
    }
} 