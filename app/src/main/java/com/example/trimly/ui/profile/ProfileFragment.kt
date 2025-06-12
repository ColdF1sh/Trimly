package com.example.trimly.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trimly.R
import android.widget.ImageView
import com.example.trimly.data.UserDao
import com.example.trimly.data.User
import com.example.trimly.ui.PhoneAuthActivity
import androidx.navigation.fragment.findNavController
import android.util.Log

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val context = requireContext()
        val prefs = context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userid = prefs.getInt("userid", -1)
        val userDao = UserDao(context)
        val user = userDao.getUserById(userid)

        val ivProfilePhoto = view.findViewById<ImageView>(R.id.ivProfilePhoto)
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileRating = view.findViewById<TextView>(R.id.tvProfileRating)
        val tvProfilePhone = view.findViewById<TextView>(R.id.tvProfilePhone)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // TODO: Додати завантаження фото користувача, якщо буде реалізовано
        if (user != null) {
            tvProfileName.text = "${user.firstName} ${user.lastName ?: ""}".trim()
            tvProfileRating.text = "Рейтинг: ${user.rating}"
            tvProfilePhone.text = user.phone
            tvProfileEmail.text = user.email
        }

        btnLogout.setOnClickListener {
            prefs.edit().remove("userid").apply()
            val intent = Intent(context, PhoneAuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val userRole = prefs.getString("role", "client")
        Log.d("ProfileFragment", "userRole = '" + userRole + "'")
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val profileContainer = view.findViewById<ViewGroup>(R.id.profileContainer) ?: view as ViewGroup

        if (userRole?.trim()?.lowercase() == "admin") {
            val btnAdmin = Button(requireContext()).apply {
                text = "Панель адміністратора"
                setBackgroundColor(resources.getColor(android.R.color.holo_blue_dark))
                setTextColor(resources.getColor(android.R.color.white))
                textSize = 16f
                setOnClickListener {
                    findNavController().navigate(R.id.navigation_admin)
                }
            }
            profileContainer.addView(btnAdmin, profileContainer.indexOfChild(btnLogout))
        }
    }
}
