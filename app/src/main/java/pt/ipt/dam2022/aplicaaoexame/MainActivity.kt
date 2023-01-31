package pt.ipt.dam2022.aplicaaoexame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import pt.ipt.dam2022.aplicaaoexame.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)
        }
    }