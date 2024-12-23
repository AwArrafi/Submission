package com.example.submission.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.submission.R
import com.example.submission.databinding.ActivityMapsBinding
import com.example.submission.di.Injection
import com.example.submission.viewmodel.StoryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            storyViewModel = Injection.provideStoryViewModel(this@MapsActivity)

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@MapsActivity)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        observeStoriesWithLocation()
    }

    private fun observeStoriesWithLocation() {
        storyViewModel.storiesWithLocation.observe(this) { stories ->
            if (stories.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                stories.forEach { story ->
                    if (story.lat != null && story.lon != null) {
                        val latLng = LatLng(story.lat, story.lon)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .snippet(story.description)
                        )
                        boundsBuilder.include(latLng)
                    }
                }
                val bounds = boundsBuilder.build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }

        storyViewModel.fetchStoriesWithLocation()
    }
}