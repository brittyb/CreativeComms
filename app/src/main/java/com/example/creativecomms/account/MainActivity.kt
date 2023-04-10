package com.example.creativecomms.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.creativecomms.HomeActivity
import com.example.creativecomms.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        auth = Firebase.auth
        FirebaseAuth.getInstance().signOut()
        val email = findViewById<EditText>(R.id.et_user_name)
        val password = findViewById<EditText>(R.id.et_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val signupButton = findViewById<Button>(R.id.btn_signUp)
        val textWrongInfo = findViewById<TextView>(R.id.text_wrongInfo)


    // When login button clicked
        loginButton.setOnClickListener {
            //Get inputs
            val emailText = email.text.toString()
            val passwordText = password.text.toString()


            if(validateFields(emailText, passwordText)){
                loginUser(emailText, passwordText)
            }else{
                //Tell user to enter information
                textWrongInfo.text = getString(R.string.enter_email_error)
            }



        }

        signupButton.setOnClickListener {
            //Go to sign up page
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LoginLog", "Logged in")
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    //Go to home page
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                } else {
                    //Notify that the login was not successful
                    Toast.makeText(this, "Email address or password incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Check to make sure fields are not empty
    private fun validateFields(email : String, pass : String) : Boolean{
        return !(email.isEmpty() || pass.isEmpty())
    }


}