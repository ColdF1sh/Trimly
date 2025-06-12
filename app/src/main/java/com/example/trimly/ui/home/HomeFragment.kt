package com.example.trimly.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.trimly.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.Marker
import com.example.trimly.data.EstablishmentDao
import timber.log.Timber
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts

data class Salon(val name: String, val lat: Double, val lng: Double, val address: String, val phone: String, val establishmentId: Int) : java.io.Serializable

class HomeFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var locationUpdateState = false
    private lateinit var establishmentDao: EstablishmentDao
    private lateinit var searchEditText: AutoCompleteTextView
    private var allSalons: List<Salon> = listOf()
    private var filteredSalons: List<Salon> = listOf()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionGranted = true
                startLocationUpdates()
                updateLocationUI()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermissionGranted = true
                startLocationUpdates()
                updateLocationUI()
            }
            else -> {
                Toast.makeText(requireContext(), "Потрібен доступ до місцезнаходження", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val testSalons = listOf(
        Salon("Тестовий салон 1", 50.4501, 30.5234, "Київ", "", 1), // Приклад координат Києва
        Salon("Тестовий салон 2", 49.8429, 24.0317, "Львів", "", 2), // Приклад координат Львова
        Salon("Тестовий салон 3", 46.4825, 30.7233, "Одеса", "", 3)  // Приклад координат Одеси
    )

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_REQUEST_INTERVAL = 10000L // 10 seconds
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 5000L // 5 seconds
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        establishmentDao = EstablishmentDao(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        searchEditText = view.findViewById(R.id.searchEditText)
        allSalons = establishmentDao.getAllEstablishments()
        filteredSalons = allSalons

        // Підказки для AutoCompleteTextView
        val suggestions = allSalons.map { it.name }.distinct()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        searchEditText.setAdapter(adapter)

        // При виборі підказки — переміщення до найближчого закладу з такою назвою
        searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            moveToNearestSalon(selectedName)
        }

        // При натисканні Enter — переміщення до найближчого закладу з такою назвою
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    moveToNearestSalon(query)
                }
                true
            } else {
                false
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    lastKnownLocation = location
                    updateMapLocation()
                    // Stop location updates once we have a location
                    stopLocationUpdates()
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.locationFab).setOnClickListener {
            getDeviceLocation()
            updateMapLocation()
        }

        // Перевірка та запит дозволу на геолокацію одразу при запуску фрагмента
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            locationPermissionGranted = true
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
        updateLocationUI()
        getDeviceLocation()
        addSalonsMarkers()
    }

    private fun addSalonsMarkers() {
        if (!::map.isInitialized) {
            Timber.w("Map is not initialized")
            return
        }
        map.clear()
        Timber.i("Починаємо додавання маркерів закладів")
        val salonsToShow = filteredSalons
        Timber.i("Отримано закладів для відображення: ${salonsToShow.size}")
        for (salon in salonsToShow) {
            val latLng = LatLng(salon.lat, salon.lng)
            Timber.i("Додаємо маркер для закладу: ${salon.name} на координатах: $latLng")
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(salon.name)
                    .snippet("${salon.address}\n${salon.phone}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )?.tag = salon
        }
        Timber.i("Маркери додано успішно")
    }

    private fun updateMapMarkers() {
        addSalonsMarkers()
    }

    private fun startLocationUpdates() {
        if (!locationPermissionGranted) return
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_REQUEST_INTERVAL
            )
                .setMinUpdateIntervalMillis(LOCATION_REQUEST_FASTEST_INTERVAL)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                locationUpdateState = true
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        locationUpdateState = false
    }

    private fun updateLocationUI() {
        try {
            if (::map.isInitialized) {
                if (locationPermissionGranted) {
                    map.isMyLocationEnabled = true
                    map.uiSettings.isMyLocationButtonEnabled = true
                } else {
                    map.isMyLocationEnabled = false
                    map.uiSettings.isMyLocationButtonEnabled = false
                    lastKnownLocation = null
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            lastKnownLocation = it
                            updateMapLocation()
                        } ?: run {
                            // If lastLocation is null, request location updates
                            startLocationUpdates()
                            Toast.makeText(requireContext(), "Отримання місцезнаходження...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Помилка отримання місцезнаходження: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Помилка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMapLocation() {
        if (!::map.isInitialized) {
            Timber.w("Map is not initialized")
            return
        }
        
        lastKnownLocation?.let { location ->
            try {
                val latLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        DEFAULT_ZOOM
                    )
                )
            } catch (e: Exception) {
                Timber.e(e, "Error updating map location")
                Toast.makeText(requireContext(), "Помилка оновлення карти: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Timber.w("Location is null")
            Toast.makeText(requireContext(), "Місцезнаходження недоступне", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationPermissionGranted && !locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationUpdateState) {
            stopLocationUpdates()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val salon = marker.tag as? Salon
        if (salon != null) {
            val bottomSheet = SalonDetailsBottomSheetFragment.newInstance(salon)
            bottomSheet.show(childFragmentManager, "SalonDetails")
            return true
        }
        return false
    }

    private fun moveToNearestSalon(query: String) {
        val matchingSalons = allSalons.filter { it.name.contains(query, ignoreCase = true) }
        if (matchingSalons.isNotEmpty()) {
            val nearestSalon = matchingSalons.first()
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(nearestSalon.lat, nearestSalon.lng),
                    DEFAULT_ZOOM
                )
            )
        }
    }
} 