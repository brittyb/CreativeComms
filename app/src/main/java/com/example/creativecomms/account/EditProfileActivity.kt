package com.example.creativecomms.account

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.creativecomms.HomeActivity
import com.example.creativecomms.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var profilePic : CircleImageView
    private val pickImage = 100
    private var uri: Uri? = null
    lateinit var user : User
    val uid = FirebaseAuth.getInstance().uid ?: ""
    private var currentPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val newUsername = findViewById<EditText>(R.id.et_new_username)
        val password = findViewById<EditText>(R.id.et_password)
        val newPassword = findViewById<EditText>(R.id.et_new_password)
        val newPasswordRepeat = findViewById<EditText>(R.id.et_new_password2)

        val changeUserBtn = findViewById<Button>(R.id.edit_user_btn)
        val changePassBtn = findViewById<Button>(R.id.edit_password_btn)
        val changePfpBtn = findViewById<Button>(R.id.edit_pic_btn)
        val saveChangesBtn = findViewById<Button>(R.id.save_changes_button)

        val errText = findViewById<TextView>(R.id.errorText)

        val newUsernameText = newUsername?.text
        val passwordText = password?.text
        val newPasswordText = newPassword?.text
        val newPasswordRepeatText = newPasswordRepeat?.text

        var correctNewUsr = ""
        var correctNewPassword = ""

        profilePic = findViewById<CircleImageView>(R.id.profileImage)
        Log.d("DatabaseLog", "Edit " + uid)
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User from firebase
                user = dataSnapshot.getValue(User::class.java)!!

                //get profile uri
                Glide.with(applicationContext).load(user.profileImageUri).into(profilePic)
                correctNewPassword = user.password.toString()
                correctNewUsr = user.username.toString()
                changePassBtn.setOnClickListener {
                    if(passwordText.toString() == correctNewPassword){
                        if(newPasswordText.toString() == newPasswordRepeatText.toString()){
                            if(newPasswordText?.length!! <= 5){
                                errText.text = "Password must be at least 6 characters"

                            }else{
                                if(newPasswordText?.contains(" ") == true){
                                    errText.text = "Password must not contain spaces"
                                }else{
                                    correctNewPassword = newPasswordText.toString()
                                    errText.text = "New password is valid. Press save changes to save password"
                                }
                            }
                        }else{
                            errText.text = "New passwords do not match"
                        }
                    }else{
                       errText.text = "Current password is incorrect"
                    }
                }

                changeUserBtn.setOnClickListener {
                    var isTaken = false
                    val usersDatabase = FirebaseDatabase.getInstance().getReference("/users")
                    val usernameListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (user in snapshot.children) {
                                val user = user.getValue(User::class.java)
                                if(user?.username == newUsernameText.toString()){
                                    errText.text = "Username already in use"
                                    isTaken = true
                                }
                            }
                            if(!isTaken){
                                correctNewUsr = newUsernameText.toString()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    usersDatabase.addListenerForSingleValueEvent(usernameListener)
                }

                saveChangesBtn.setOnClickListener {
                    val usernameRef = FirebaseDatabase.getInstance().getReference("/users/$uid/username")
                    usernameRef.setValue(correctNewUsr)
                    val passwordRef = FirebaseDatabase.getInstance().getReference("/users/$uid/password")
                    passwordRef.setValue(correctNewPassword)
                    saveChanges() }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        database.addListenerForSingleValueEvent(userListener)



        changePfpBtn.setOnClickListener {
            Log.d("ProfilePictureActivity", "Pfp button clicked")
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        }

    private fun saveChanges(){
        uploadImageToFirebaseStorage()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            uri = data?.data
            Glide.with(applicationContext).load(uri).into(profilePic)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if(uri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("pfps/$filename")
        ref.putFile(uri!!)
            .addOnSuccessListener {
                Log.d("EditProfileActivity", "Successfully uploaded pfp image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("EditProfileActivity", it.toString())
                    savePictureToFirebase(it.toString())
                }
            }
    }



    private fun savePictureToFirebase(uri : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/profileImageUri")
        ref.setValue(uri).addOnSuccessListener { Log.d("EditProfileActivity", "Successfully set value") }
    }

}