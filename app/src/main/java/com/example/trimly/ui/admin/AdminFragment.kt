package com.example.trimly.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.trimly.R
import com.google.android.material.card.MaterialCardView

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar?>(R.id.toolbar)
        toolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar?.setNavigationOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }

        // Setup click listeners for each management card
        view.findViewById<MaterialCardView>(R.id.cardEstablishments).setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_manageEstablishments)
        }

        view.findViewById<MaterialCardView>(R.id.cardMasters).setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_manageMasters)
        }

        view.findViewById<MaterialCardView>(R.id.cardServices).setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_manageServices)
        }

        view.findViewById<MaterialCardView>(R.id.cardBookings).setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_manageSessions)
        }
    }
} 