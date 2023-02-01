package pt.ipt.dam2022.aplicaaoexame

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

        val LAT : String = "39.4647"
        val LON : String = "-8.469"
        val API : String = "f4e820447d3f6dc3e5581f68d547ef74"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        weatherTask().execute()
    }
    inner class weatherTask() : AsyncTask<String, Void, String>()

    {
        override fun onPreExecute() {
            super.onPreExecute()

            findViewById<ProgressBar>(R.id.progresso).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.Container1).visibility = View.GONE
            findViewById<TextView>(R.id.erro).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response:String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?lang=pt&units=metric&lat=$LAT&lon=$LON&appid=$API").readText(Charsets.UTF_8)
            }
            catch (e: Exception)
            {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val update:Long = jsonObj.getLong("dt")
                val updatet = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(update*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressao = main.getString("pressure")
                val humidade= main.getString("humidity")
                val nsol:Long = sys.getLong("sunrise")
                val psol:Long = sys.getLong("sunset")
                val vvento = wind.getString("speed")
                val descricaotempo = weather.getString("description")

                val local = jsonObj.getString("name")+", "+sys.getString("country")


                findViewById<TextView>(R.id.Localizacao).text = local
                findViewById<TextView>(R.id.update1).text =  updatet
                findViewById<TextView>(R.id.Tempo).text = descricaotempo.capitalize()
                findViewById<TextView>(R.id.Temperatura).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax


                findViewById<ProgressBar>(R.id.progresso).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.Container1).visibility = View.VISIBLE

            }
            catch (e: Exception)
            {
                findViewById<ProgressBar>(R.id.progresso).visibility = View.GONE
                findViewById<TextView>(R.id.erro).visibility = View.VISIBLE
            }
        }

    }
}