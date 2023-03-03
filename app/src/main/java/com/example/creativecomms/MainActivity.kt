package com.example.creativecomms

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        auth = Firebase.auth

        var email = findViewById<EditText>(R.id.et_user_name)
        var epassword = findViewById<EditText>(R.id.et_password)
        var loginButton = findViewById<Button>(R.id.btn_login)
        var signupButton = findViewById<Button>(R.id.btn_signUp)
        var textWrongInfo = findViewById<TextView>(R.id.text_wrongInfo)


    // set on-click listener
        loginButton.setOnClickListener {
            val emailText = email.text.toString();
            val passwordText = epassword.text.toString();


            if(validateFields(emailText, passwordText)){
                loginUser(emailText, passwordText)
            }else{
                textWrongInfo.text = "Enter an email address and password"
            }



        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


    }

    public fun loginUser(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Unable to login. Check your input or try again later", Toast.LENGTH_SHORT).show()
                }
            }
    }

    public fun validateFields(email : String, pass : String) : Boolean{
        return !(email.isEmpty() || pass.isEmpty())
    }


}