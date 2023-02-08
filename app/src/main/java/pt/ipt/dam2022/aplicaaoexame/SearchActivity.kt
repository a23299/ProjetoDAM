package pt.ipt.dam2022.aplicaaoexame

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.ExtractedTextRequest
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class SearchActivity : AppCompatActivity() {

    private lateinit var procurarCity: EditText
    private lateinit var procurarBT: Button
    private lateinit var voltarBT: Button
    private lateinit var lastSearch: TextView
    private var city: String = ""
    //val searches = ArrayList<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //Referencia para a BD
        val myRef = FirebaseDatabase.getInstance("https://aplicacaoexame-default-rtdb.europe-west1.firebasedatabase.app").getReference("strings")

        lastSearch = findViewById(R.id.lastSearch)
        procurarCity = findViewById(R.id.procurar)
        procurarBT = findViewById(R.id.procurar_button)
        voltarBT = findViewById(R.id.voltarBT)

        //Procurar tempo na cidade pesquisada
        procurarBT.setOnClickListener{
            if(TextUtils.isEmpty(procurarCity.text)) {
                Toast.makeText(this, "Escreva Algo", Toast.LENGTH_SHORT).show()
            }else{
                city = procurarCity.text.toString()
                Toast.makeText(this, city, Toast.LENGTH_SHORT).show()
                //ir para o WeatherActivityCity
                val tempoIntent = Intent(this, WeatherActivityCity::class.java)
                //levar o valor da cidade pesquisada para o WeatherActivityCity
                tempoIntent.putExtra(null, city)
                startActivity(tempoIntent)
                //definir valor da ultima cidade pesquisada na BD
                myRef.setValue(getCity())
            }
        }

        //voltar para o Main Activity
        voltarBT.setOnClickListener {
            val voltarIntent = Intent(this, MainActivity::class.java)
            startActivity(voltarIntent)
        }

        //ir buscar o ultimo valor colocado na barra de pesquisa e colocalo no TextView
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue<String>()
                lastSearch.text = value
            }
            //se houver algum erro ao ler o valor informar o utilizador
            override fun onCancelled(error: DatabaseError) {
                //falha na leitura do valor
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    //retornar o valor colocado na barra de pesquisa
    @JvmName("getCity1")
    fun getCity(): String {
        return city
    }
}