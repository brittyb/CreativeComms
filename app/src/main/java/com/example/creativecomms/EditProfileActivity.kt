package com.example.creativecomms

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profilePic = findViewById<CircleImageView>(R.id.profileImage)

        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User from firebase
                user = dataSnapshot.getValue(User::class.java)!!

                //get profile uri
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUri.toString())
                Log.d("DatabaseLog", user.profileImageUri.toString())
                val MAX_SIZE = 1024*1024*5
                imageRef.getBytes(MAX_SIZE.toLong())
                    .addOnSuccessListener { data ->
                        // Convert the retrieved data to a Bitmap
                        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                        profilePic.setImageBitmap(bitmap)
                        // Use the bitmap as needed
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                        Log.d("DatabaseLog", "Did not get bytes")
                    }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        database.addListenerForSingleValueEvent(userListener)


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
        //val newPasswordRepeatText = newPasswordRepeat?.text



        /*


            changeUserBtn.setOnClickListener {
                if (passwordText.toString() == currentAccountPass) {
                    if (newUsernameText?.length!! <= 3) {
                        errText.text = "Username must contain at least 4 characters"
                    } else {
                        newCorrectUser = newUsernameText.toString()
                        errText.text = "Username changed successfully"
                    }
                } else {
                    errText.text = "Current password is incorrect"
                }
            }
*/

        /*
                changePassBtn.setOnClickListener {
                    if (passwordText.toString() == currentAccountPass) {
                        if (newPasswordText?.contains(" ") == true) {
                            errText.text = "New password cannot contain spaces"
                        } else if (newPasswordText?.length!! <= 5) {
                            errText.text = "New password must contain at least 6 characters"
                        } else if (newPasswordText.toString() != newPasswordRepeatText.toString()) {
                            errText.text = "Passwords do not match"
                        } else {
                            errText.text = "Password changed successfully"
                            newCorrectPass = newPasswordText.toString()

                        }

                    } else {
                        errText.text = "Current password is incorrect"
                    }
                }
*/
                changePfpBtn.setOnClickListener {
                    Log.d("ProfilePictureActivity", "Pfp button clicked")
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
                }

                saveChangesBtn.setOnClickListener {
                    //account.username = newCorrectUser
                    //account.password = newCorrectPass
                    uploadImageToFirebaseStorage()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            profilePic?.setImageBitmap(bitmap)
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
                    Log.d("EditProfileActivity", "${it.toString()}")
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