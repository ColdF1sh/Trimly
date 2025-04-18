package com.example.trimly

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trimly.R
import com.example.trimly.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15f
        private const val LOCATION_REQUEST_INTERVAL = 10000L // 10 seconds
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 5000L // 5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    lastKnownLocation = location
                    updateMapLocation()
                }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Add location button
        val locationButton = FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_mylocation)
            setOnClickListener {
                getDeviceLocation()
            }
        }
        binding.root.addView(locationButton)

        getLocationPermission()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            startLocationUpdates()
            updateLocationUI()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateLocationUI()
        getDeviceLocation()
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
                    this,
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
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        lastKnownLocation = it
                        updateMapLocation()
                    }
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMapLocation() {
        lastKnownLocation?.let { location ->
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    DEFAULT_ZOOM
                )
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    startLocationUpdates()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
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
} 