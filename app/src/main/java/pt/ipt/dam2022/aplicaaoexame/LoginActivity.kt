package pt.ipt.dam2022.aplicaaoexame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import pt.ipt.dam2022.aplicaaoexame.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

        private lateinit var binding: ActivityLoginBinding
        private lateinit var firebaseAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            firebaseAuth = FirebaseAuth.getInstance()

            //Verificar LogIn
            binding.loginButton.setOnClickListener {
                val email = binding.loginEmail.text.toString()
                val password = binding.loginPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()){
                        //verificar se o utilizador existe na bd se existir é redirecionado para MainActivity
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, it.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                } else{
                    Toast.makeText(this, "Nao preencheu os espaços",Toast.LENGTH_SHORT).show()
                }
            }

            //Voltar para o SignUp
            binding.signupRedirect.setOnClickListener {
                val signupIntent = Intent(this, MainActivity::class.java)
                startActivity(signupIntent)
            }
        }
    }