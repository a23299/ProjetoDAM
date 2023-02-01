package pt.ipt.dam2022.aplicaaoexame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pt.ipt.dam2022.aplicaaoexame.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty())
                if (password == confirmPassword) {

                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                    }
                else {
                    Toast.makeText(this, "A password não é igual", Toast.LENGTH_SHORT).show()
                }
            else {
                Toast.makeText(this, "Não preencheu ", Toast.LENGTH_SHORT).show()
            }


        }

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

    }
}



