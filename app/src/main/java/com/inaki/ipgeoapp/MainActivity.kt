package com.inaki.ipgeoapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.inaki.ipgeoapp.databinding.ActivityMainBinding
import com.inaki.ipgeoapp.local.Location
import com.inaki.ipgeoapp.utils.State
import com.inaki.ipgeoapp.viewmodel.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val locationViewModel by lazy {
        ViewModelProvider(this)[LocationViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            locationViewModel.location.collect {
                when(it) {
                    State.LOADING -> {
                        binding.loadState.visibility = View.VISIBLE
                        binding.detailsLoc.visibility = View.GONE
                    }
                    is State.SUCCESS -> {
                        updateUI(it.data)
                    }
                    is State.ERROR -> {
                        binding.loadState.visibility = View.GONE
                        binding.detailsLoc.visibility = View.GONE
                        showError(it.error.localizedMessage) {
                            locationViewModel.findLocation()
                        }
                    }
                }
            }
        }

        binding.searchBtn.setOnClickListener {
            locationViewModel.ipAddress = binding.searchBar.text.toString()
            locationViewModel.findLocation()
        }
    }

    private fun updateUI(data: Location) {
        binding.countryNameRegion.text = String.format("${data.city}, ${data.country}")
        binding.countryCode.text = data.countryCode
        binding.countryZipcode.text = data.zipCode
        binding.locIp.text = data.ipAddress

        binding.mapClick.setOnClickListener {
            val uri = String.format(Locale.ENGLISH, "geo:%f,%f", data.latitude, data.longitude)
            Intent(Intent.ACTION_VIEW, Uri.parse(uri)).also {
                startActivity(it)
            }
        }

        binding.detailsLoc.visibility = View.VISIBLE
        binding.loadState.visibility = View.GONE
    }

    private fun showError(message: String? = "Unknown error", action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Error occurred")
            .setMessage(message)
            .setPositiveButton("Retry") { dialog, _ ->
                action()
                dialog.dismiss()
            }
            .setNegativeButton("Dismiss") {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}