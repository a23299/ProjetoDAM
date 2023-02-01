@file:Suppress("DEPRECATION")

package pt.ipt.dam2022.aplicaaoexame

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var country: TextView
    private lateinit var city: TextView
    private lateinit var lat: TextView
    private lateinit var lon: TextView
    private lateinit var currentLocationBT: Button
    private lateinit var meteriologiaBT: Button

    private val permissionRequestAccessLocation = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        country = findViewById(R.id.pais)
        city = findViewById(R.id.cidade)
        lat = findViewById(R.id.lat)
        lon = findViewById(R.id.longi)
        currentLocationBT = findViewById(R.id.button)
        meteriologiaBT = findViewById(R.id.button3)

        currentLocationBT.setOnClickListener{
            getCurrentLocation()
        }
        meteriologiaBT.setOnClickListener{
            val tempoIntent = Intent(this, WeatherActivity::class.java)
            startActivity(tempoIntent)
        }

    }

    private fun getCurrentLocation(){
        if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                fusedLocationProvider.lastLocation.addOnCompleteListener(this){
                    task-> val location: Location ?= task.result
                    if(location == null){
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                        newLocationData()
                    }
                    else{
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        country.text = getCountryName(location.latitude, location.longitude, applicationContext)
                        city.text = getCityName(location.latitude, location.longitude, applicationContext)
                        lat.text = getCurrentLatitude(applicationContext)
                        lon.text = getCurrentLongitude(applicationContext)
                    }
                }
            }
            else{requestPermission()

            }
        }
        else {
            Toast.makeText(this, "Please Enable your Location Service", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ), permissionRequestAccessLocation
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == permissionRequestAccessLocation){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getCityName(latitude: Double, longitude: Double, context: Context): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace()
            null
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            illegalArgumentException.printStackTrace()
            null
        }
        return addresses?.firstOrNull()?.locality
    }

    fun getCountryName(latitude: Double, longitude: Double, context: Context): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace()
            null
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            illegalArgumentException.printStackTrace()
            null
        }
        return addresses?.firstOrNull()?.countryName
    }

    private fun newLocationData(){
        val locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation!!
            Toast.makeText(applicationContext, "your last last location: " + lastLocation.longitude.toString() + " , " + lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()
            country.text = getCountryName(lastLocation.latitude, lastLocation.longitude, applicationContext)
            city.text = getCityName(lastLocation.latitude, lastLocation.longitude, applicationContext)
            lat.text = lastLocation.latitude.toString()
            lon.text = lastLocation.longitude.toString()
        }
    }

    fun getCurrentLatitude(context: Context): String? {
        val location = getLocation(context)
        return location?.latitude?.toString()
    }

    fun getCurrentLongitude(context: Context): String? {
        val location = getLocation(context)
        return location?.longitude?.toString()
    }

    private fun getLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            requestPermission()
        } else {
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return null
    }
}