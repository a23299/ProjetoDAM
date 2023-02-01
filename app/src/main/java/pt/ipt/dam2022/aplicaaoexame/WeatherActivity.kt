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

        val CITY : String = "dhaka,db"
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
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
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
                val sys = jsonObj.getJSONObject("sys")
                val main = jsonObj.getJSONObject("main")
                val tempo = jsonObj.getJSONArray("tempo").getJSONObject(0)
                val Uupdate:Long = jsonObj.getLong("data")
                val Uupdatet = "Atualizado pela ultima vez em:" + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(Uupdate*1000))
                val temp = main.getString("Temp")+"ºC"
                val tempMin = "Min: "+ main.getString("temp_min")+"ºC"
                val tempMax = "Min: "+ main.getString("temp_max")+"ºC"
                val tempoDescicao = tempo.getString("descricao")
                val localizacao = jsonObj.getString("nome") + ", " + sys.getString("Pais")

                findViewById<TextView>(R.id.Localizacao).text = localizacao
                findViewById<TextView>(R.id.update1).text = Uupdatet
                findViewById<TextView>(R.id.Tempo).text = tempoDescicao.capitalize()
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.Temperatura).text = temp

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