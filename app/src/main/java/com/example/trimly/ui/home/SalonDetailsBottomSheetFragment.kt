package com.example.trimly.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trimly.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.util.DisplayMetrics
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import android.util.Log
import android.view.Display
import android.os.Build

class SalonDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private var salon: Salon? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var photosRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            val serializable = it.getSerializable(ARG_SALON)
            if (serializable is Salon) {
                salon = serializable
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_salon_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        val windowManager = requireActivity().windowManager
        @Suppress("DEPRECATION")
        val display = windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.65).toInt()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = desiredHeight
        BottomSheetBehavior.from(bottomSheet!!).apply {
            peekHeight = desiredHeight
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(requireContext())

        photosRecyclerView = view.findViewById(R.id.rvSalonPhotos)
        photosRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        salon?.let { selectedSalon ->
            view.findViewById<TextView>(R.id.tvSalonName).text = selectedSalon.name
            view.findViewById<TextView>(R.id.tvAddress).text = selectedSalon.address
            view.findViewById<TextView>(R.id.tvPhone).apply {
                visibility = if (selectedSalon.phone.isNotEmpty()) View.VISIBLE else View.GONE
                text = "Телефон: ${selectedSalon.phone}"
            }

            view.findViewById<Button>(R.id.btnBook).setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToBookingFragment(selectedSalon)
                findNavController().navigate(action)
                dismiss()
            }

            val bounds = RectangularBounds.newInstance(
                LatLng(selectedSalon.lat - 0.05, selectedSalon.lng - 0.05),
                LatLng(selectedSalon.lat + 0.05, selectedSalon.lng + 0.05)
            )
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(selectedSalon.name)
                .setLocationBias(bounds)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val prediction = response.autocompletePredictions.firstOrNull()
                    val placeId = prediction?.placeId
                    Log.d("PLACES", "Found placeId: $placeId for ${selectedSalon.name}")
                    if (placeId != null) {
                        loadPlacePhotos(placeId)
                    } else {
                        Log.d("PLACES", "No placeId found for ${selectedSalon.name}")
                    }
                }
                .addOnFailureListener {
                    Log.d("PLACES", "Error finding placeId: ${it.message}")
                }
        }
    }

    private fun loadPlacePhotos(placeId: String) {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val photoMetadatas = place.photoMetadatas
                if (photoMetadatas != null && photoMetadatas.isNotEmpty()) {
                    photosRecyclerView.adapter = SalonPhotosAdapter(placesClient, photoMetadatas)
                } else {
                    Log.d("PLACES", "No photos found for placeId: $placeId")
                }
            }
            .addOnFailureListener {
                Log.d("PLACES", "Error fetching place details: ${it.message}")
            }
    }

    companion object {
        private const val ARG_SALON = "salon"
        fun newInstance(salon: Salon): SalonDetailsBottomSheetFragment {
            val fragment = SalonDetailsBottomSheetFragment()
            val args = Bundle()
            args.putSerializable(ARG_SALON, salon)
            fragment.arguments = args
            return fragment
        }
    }
} 