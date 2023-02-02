package pt.ipt.dam2022.aplicaaoexame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SearchActivity : AppCompatActivity() {

    lateinit var procurarCity: EditText
    private lateinit var procurarBT: Button
    private lateinit var voltarBT: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        procurarCity = findViewById(R.id.procurar)
        procurarBT = findViewById(R.id.procurar_button)
        voltarBT = findViewById(R.id.voltarBT)

        procurarBT.setOnClickListener{
            if(TextUtils.isEmpty(procurarCity.text)) {
                Toast.makeText(this, "Escreva Algo", Toast.LENGTH_SHORT).show()
            }else{
                val tempoIntent = Intent(this, WeatherActivityCity::class.java)
                startActivity(tempoIntent)
            }
        }

        voltarBT.setOnClickListener {
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
        }

    }
    fun getCity(): String {
        return procurarCity.text.toString()
    }
}