@file:Suppress("DEPRECATION")

package pt.ipt.dam2022.aplicaaoexame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import pt.ipt.dam2022.aplicaaoexame.databinding.ActivityWeatherBinding
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding
    var lat: String = "39.4647"
    var lon: String = "-8.469"
    val api: String = "f4e820447d3f6dc3e5581f68d547ef74"
    private var main = MainActivity()
    private lateinit var voltarBT: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        getCoordinates(main.latitudeTV(), main.longitudeTV())
        WeatherTask().execute()

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.voltarBT.setOnClickListener {
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
        }

    }


    private fun getCoordinates(){
        lat = main.getCurrentLatitude(applicationContext).toString()
        lon = main.getCurrentLongitude(applicationContext).toString()
    }

    private fun getCoordinates(latitude: String, longitude: String){
        lat = latitude
        lon = longitude
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask() : AsyncTask<String, Void, String>() {

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()

            findViewById<ProgressBar>(R.id.progresso).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.Container1).visibility = View.GONE
            findViewById<TextView>(R.id.erro).visibility = View.GONE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: String?): String? {
            val response: String? = try {
                URL("https://api.openweathermap.org/data/2.5/weather?lang=pt&units=metric&lat=$lat&lon=$lon&appid=$api").readText(
                    Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
            return response
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result.toString())
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val update: Long = jsonObj.getLong("dt")
                val updateTempo =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(update * 1000))
                val temperatura = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressao = main.getString("pressure")
                val humidade = main.getString("humidity")
                val nascerSol: Long = sys.getLong("sunrise")
                val porSol: Long = sys.getLong("sunset")
                val ventoSpeed = wind.getString("speed")
                val descTempo = weather.getString("description")
                val groupTempo = weather.getString("main")

                val local = jsonObj.getString("name") + ", " + sys.getString("country")


                findViewById<TextView>(R.id.Localizacao).text = local
                findViewById<TextView>(R.id.update1).text = updateTempo
                findViewById<TextView>(R.id.Tempo).text = descTempo.capitalize(Locale.ROOT)
                findViewById<TextView>(R.id.Temperatura).text = temperatura
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax

                setWeatherConditionIcon(findViewById<ImageView>(R.id.weather_icon), groupTempo)

                findViewById<ProgressBar>(R.id.progresso).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.Container1).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.progresso).visibility = View.GONE
                findViewById<TextView>(R.id.erro).visibility = View.VISIBLE
            }
        }

    }

    fun setWeatherConditionIcon(imageView: ImageView, weatherCondition: String) {
        when (weatherCondition) {
            "Clear" -> imageView.setImageResource(R.drawable.clear)
            "Rain" -> imageView.setImageResource(R.drawable.rain)
            "Snow" -> imageView.setImageResource(R.drawable.snow)
            "Thunderstorm" -> imageView.setImageResource(R.drawable.thunder)
            "Drizzle" -> imageView.setImageResource(R.drawable.rain)
            "Atmosphere" -> imageView.setImageResource(R.drawable.atmosphere)
            "Clouds" -> imageView.setImageResource(R.drawable.cloud)
            else -> imageView.setImageResource(R.drawable.unknown)
        }

    }

}