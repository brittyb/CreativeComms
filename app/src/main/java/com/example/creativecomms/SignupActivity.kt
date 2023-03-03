package com.example.creativecomms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private var username : EditText? = null
    private var email : EditText? = null
    private var password : EditText? = null
    private var artistBox: CheckBox? = null
    private lateinit var user :User
    private var ratingArray : MutableList<Float> = mutableListOf<Float>()
    private var commArray : MutableList<Commission> = mutableListOf<Commission>()
    private var downloadedUrl : Uri? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        val signupButton = findViewById<Button>(R.id.signup_button)
        username = findViewById<EditText>(R.id.et_username)
        email = findViewById<EditText>(R.id.et_email)
        password = findViewById<EditText>(R.id.et_password)
        val artistBox = findViewById<CheckBox>(R.id.checkBox)
        val textErrorMessage = findViewById<TextView>(R.id.errorText)

        val usernameText = username?.text
        val emailText = email?.text
        val passwordText = password?.text

        getDefaultProfile()



        signupButton.setOnClickListener {

            //check if username is empty
            if (usernameText != null) {
                //check if password is empty
                if (passwordText != null) {
                    //check if email address is valid
                    if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {

                        //Check if password contains spaces
                        if(passwordText.contains(" ")){
                            textErrorMessage.text = "Password cannot contain spaces"
                        }else if(passwordText.length <= 5 || usernameText.length <=3){
                            textErrorMessage.text = "Username must contain at least 4 characters and password must contain at least 6"
                        }else{
                            //Go to home page and create account

                            //create account from Account class

                            registerUser(emailText.toString(), passwordText.toString())
                            Log.d("RegisterActivity", "Registered User")
                            saveUserToFirebaseDatabase()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }

                    }else{
                        //error text for invalid email address
                        textErrorMessage.text = "Please enter a valid email address"
                    }
                }else{
                    //error text for blank password
                    textErrorMessage.text = "Please enter a password"
                }
            }else{
                //error for blank username
                textErrorMessage.text = "Please enter a username"
            }
        }


    }

    private fun registerUser(email: String, password : String){
        auth.createUserWithEmailAndPassword(email, password)
    }

    private fun saveUserToFirebaseDatabase(){

        val uid = FirebaseAuth.getInstance().uid
        Log.d("SignUpLog", uid.toString())
        Log.d("RegisterActivity", uid.toString())
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        user  = User(uid, username?.text.toString(),
                email?.text.toString(), password?.text.toString(), 0.0f, downloadedUrl.toString())
        ref.setValue(user)
    }



    private fun getDefaultProfile(){

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        storageRef.child("defaultprofile.jpg").downloadUrl.addOnSuccessListener {
            downloadedUrl = it
        }.addOnFailureListener {
            // Handle any errors
            Log.d("ProfileImage", "Did not get profile")
        }
    }



}