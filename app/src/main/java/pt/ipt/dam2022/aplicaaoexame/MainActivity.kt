@file:Suppress("DEPRECATION")

package pt.ipt.dam2022.aplicaaoexame

import android.Manifest
import android.annotation.SuppressLint
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
    lateinit var lat: TextView
    lateinit var lon: TextView

    private lateinit var currentLocationBT: Button
    private lateinit var meteriologiaBT: Button
    private lateinit var loadLocalBT: Button
    private lateinit var searchCityBT: Button

    private lateinit var countryString: String
    private lateinit var cityString: String
    private lateinit var latString: String
    private lateinit var longString: String

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
        loadLocalBT = findViewById(R.id.button4)
        searchCityBT = findViewById(R.id.button2)

        //ir buscar a localização atual
        currentLocationBT.setOnClickListener{
            getCurrentLocation()
        }

        //ir para Weather Activity
        meteriologiaBT.setOnClickListener{
            val tempoIntent = Intent(this, WeatherActivity::class.java)
            startActivity(tempoIntent)
        }

        //ir para Search Activity
        searchCityBT.setOnClickListener{
            val tempoIntent = Intent(this, SearchActivity::class.java)
            startActivity(tempoIntent)
        }

        //ir buscar ultima localização procurada
        loadLocalBT.setOnClickListener{
            loadLocation()
        }

    }

    //retornar a localização atual
    private fun getCurrentLocation(){
        if(isLocationEnabled()){
                //verificar permissoes
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                fusedLocationProvider.lastLocation.addOnCompleteListener(this){
                    task-> val location: Location ?= task.result
                    //verificar se location é null
                    if(location == null){
                        Toast.makeText(this, "Null Received", Toast.LENGTH_SHORT).show()
                        newLocationData()
                    }
                    //se location for notNull
                    else{
                        Toast.makeText(this, "Get Success", Toast.LENGTH_SHORT).show()
                        //colocar valores de localização nos TextViews apropriados
                        country.text = getCountryName(location.latitude, location.longitude, applicationContext)
                        city.text = getCityName(location.latitude, location.longitude, applicationContext)
                        lat.text = getCurrentLatitude(applicationContext)
                        lon.text = getCurrentLongitude(applicationContext)
                        //colocar coordenadas em variaveis: String
                        coordinatesString()
                        //guardar valores
                        saveData()
                    }
                }
            }
            else{
                //pedir permissoes
                requestPermission()
            }
        }
        else {
            Toast.makeText(this, "Please Enable your Location Service", Toast.LENGTH_SHORT).show()
            //se a localização não estiver ativa, abrir as defenições do telemóveis na secção da localização
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    //verificar se GPS está ligado
    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //pedir permissions
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ), permissionRequestAccessLocation
        )
    }

    //resultado do pedido das permisões
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //resultado do pedido das permissões
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //se o resultados do pedido for o correto avisar o utilizador
        if(requestCode == permissionRequestAccessLocation){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //retornar cidade atual dado as coordenadas de latitude e longitude
    fun getCityName(latitude: Double, longitude: Double, context: Context): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = try {
            //usar o geocoder com as coordenadas
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            //apanhar problemas derivados de rede ou I/O
            ioException.printStackTrace()
            null
        } catch (illegalArgumentException: IllegalArgumentException) {
            //apanha valores de latitude e/ou longitude inválidos
            illegalArgumentException.printStackTrace()
            null
        }
        return addresses?.firstOrNull()?.locality
    }

    //retornar país atual dado as coordenadas de latitude e longitude
    fun getCountryName(latitude: Double, longitude: Double, context: Context): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (ioException: IOException) {
            //apanhar problemas derivados de rede ou I/O
            ioException.printStackTrace()
            null
        } catch (illegalArgumentException: IllegalArgumentException) {
            //apanha valores de latitude e/ou longitude inválidos
            illegalArgumentException.printStackTrace()
            null
        }
        return addresses?.firstOrNull()?.countryName
    }

    //nova Localização
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

    //última localização conhecida
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation!!
            Toast.makeText(applicationContext, "your last last location: " + lastLocation.longitude.toString() + " , " + lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()
            //colocar valores de localização nos TextViews apropriados
            country.text = getCountryName(lastLocation.latitude, lastLocation.longitude, applicationContext)
            city.text = getCityName(lastLocation.latitude, lastLocation.longitude, applicationContext)
            lat.text = lastLocation.latitude.toString()
            lon.text = lastLocation.longitude.toString()
            //colocar valores nas variáveis: String
            coordinatesString()
            //guardar dados
            saveData()
        }
    }

    //retornar latitude atual
    fun getCurrentLatitude(context: Context): String? {
        val location = getLocation(context)
        return location?.latitude?.toString()
    }

    //retornar logitude atual
    fun getCurrentLongitude(context: Context): String? {
        val location = getLocation(context)
        return location?.longitude?.toString()
    }

    //Verificar permissões
    private fun getLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            //pedir permissões
            requestPermission()

        } else {
            //se as devidas permissões forem garantidas retorna localizaçao
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return null
    }

    //colocar coordenadas em variaveis: String
    private fun coordinatesString(){
        countryString = country.text.toString()
        cityString = city.text.toString()
        latString = lat.text.toString()
        longString = lon.text.toString()
    }

    //guardar dados no ficheiro local "prefs_file"
    @SuppressLint("CommitPrefEdits")
    private fun saveData(){
        //ficheiro localizacao
        val sharedPreferences = getSharedPreferences("localizacao", Context.MODE_PRIVATE)
        //editar ficheiro
        val editor = sharedPreferences.edit()
        //colocar Strings no ficheiro
        editor.apply{
            putString("country", countryString)
            putString("city", cityString)
            putString("latitude", latString)
            putString("longitude", longString)
        }.apply()
        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
    }

    //ir buscar País ao ficheiro "localizacao"
    private fun loadCountry(): String {
        //ficheiro localizacao
        val sharedPreferences = getSharedPreferences("localizacao", Context.MODE_PRIVATE)
        //ir buscar a String do país
        val savedString = sharedPreferences.getString("country", null)

        return savedString.toString()
    }

    //ir buscar cidade ao ficheiro "localizacao"
    private fun loadCity(): String {
        //ficheiro localizacao
        val sharedPreferences = getSharedPreferences("localizacao", Context.MODE_PRIVATE)
        //ir buscar a String da cidade
        val savedString = sharedPreferences.getString("city", null)

        return savedString.toString()
    }

    //ir buscar latitude ao ficheiro "localizacao"
    private fun loadLatitude(): String {
        //ficheiro localizacao
        val sharedPreferences = getSharedPreferences("localizacao", Context.MODE_PRIVATE)
        //ir buscar a String da latitude
        val savedString = sharedPreferences.getString("latitude", null)

        return savedString.toString()
    }

    //ir buscar longitude ao ficheiro "localizacao"
    private fun loadLongitude(): String {
        //ficheiro localizacao
        val sharedPreferences = getSharedPreferences("localizacao", Context.MODE_PRIVATE)
        //ir buscar a String da longitude
        val savedString = sharedPreferences.getString("longitude", null)

        return savedString.toString()
    }

    //colocar dados da localização guardada no ficheiro "localizacao" nos sitios
    private fun loadLocation(){
        country.text = loadCountry()
        city.text = loadCity()
        lat.text = loadLatitude()
        lon.text = loadLongitude()
    }

}