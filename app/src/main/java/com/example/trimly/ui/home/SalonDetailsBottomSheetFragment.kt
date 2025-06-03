package com.example.trimly.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.trimly.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.util.DisplayMetrics
import androidx.navigation.fragment.findNavController
import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import android.util.Log

class SalonDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private var salon: Salon? = null
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val desiredHeight = (screenHeight * 0.65).toInt()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = desiredHeight
        BottomSheetBehavior.from(bottomSheet!!).apply {
            peekHeight = desiredHeight
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        // 1. Ініціалізуємо Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(requireContext())

        val imageView = view.findViewById<ImageView>(R.id.ivSalonPhoto)
        imageView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

        salon?.let { selectedSalon ->
            view.findViewById<TextView>(R.id.tvSalonName).text = selectedSalon.name
            view.findViewById<TextView>(R.id.tvAddress).text = selectedSalon.address
            view.findViewById<TextView>(R.id.tvPhone).apply {
                visibility = if (selectedSalon.phone.isNotEmpty()) View.VISIBLE else View.GONE
                text = "Телефон: ${selectedSalon.phone}"
            }

            // Збільшуємо радіус пошуку
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
                        loadPlacePhoto(placeId, imageView)
                    } else {
                        Log.d("PLACES", "No placeId found for ${selectedSalon.name}")
                    }
                }
                .addOnFailureListener {
                    Log.d("PLACES", "Error finding placeId: ${it.message}")
                    imageView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                }

            view.findViewById<Button>(R.id.btnBook).setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToBookingFragment(selectedSalon)
                findNavController().navigate(action)
                dismiss()
            }
        }
    }

    private fun loadPlacePhoto(placeId: String, imageView: ImageView) {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val photoMetadata = place.photoMetadatas?.firstOrNull()
                Log.d("PLACES", "Photo metadata: $photoMetadata")
                if (photoMetadata != null) {
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(600)
                        .setMaxHeight(400)
                        .build()
                    placesClient.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse ->
                            val bitmap = fetchPhotoResponse.bitmap
                            imageView.setImageBitmap(bitmap)
                        }
                        .addOnFailureListener {
                            Log.d("PLACES", "Error loading photo: ${it.message}")
                            imageView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                        }
                } else {
                    Log.d("PLACES", "No photo metadata for placeId: $placeId")
                    imageView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                }
            }
            .addOnFailureListener {
                Log.d("PLACES", "Error fetching place details: ${it.message}")
                imageView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
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