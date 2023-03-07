package com.example.creativecomms

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ViewCommissionActivity : AppCompatActivity() {
    lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_commission)

        //get text and image elements
        val title = findViewById<TextView>(R.id.titleText)
        val pfp = findViewById<CircleImageView>(R.id.profileImage)
        val username = findViewById<TextView>(R.id.usernameText)
        val rating = findViewById<RatingBar>(R.id.ratingBar)
        val image = findViewById<ImageView>(R.id.imageView)
        val description = findViewById<TextView>(R.id.descriptionText)
        val medium = findViewById<TextView>(R.id.mediumText)
        val time = findViewById<TextView>(R.id.timeText)
        val min = findViewById<TextView>(R.id.minPriceText)
        val max = findViewById<TextView>(R.id.maxPriceText)
        //double check to see if the commission exists before displaying
        if(intent.getSerializableExtra("Commission") != null){
            //get commission from intent
            val commission = intent.getSerializableExtra("Commission") as Commission
            //set values
            title.text = commission.title

            //Get uid information from commission uid
            val commUID = commission.uid
            val database = FirebaseDatabase.getInstance().getReference("/users/$commUID")
            //Get current user and display username and rating
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // get user data from uid
                    user = dataSnapshot.getValue(User::class.java)!!
                    //set username text
                    username.text = user.username
                    //get pfp from uid
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUri.toString())
                    //val imageRef = storageRef.child()
                    Log.d("DatabaseLog", user.profileImageUri.toString())
                    val MAX_SIZE = 1024*1024 * 5
                    imageRef.getBytes(MAX_SIZE.toLong())
                        .addOnSuccessListener { data ->
                            // Convert the retrieved data to a Bitmap
                            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                            pfp.setImageBitmap(bitmap)
                            // Use the bitmap as needed
                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors
                            Log.d("DatabaseLog", "Did not get bytes")
                        }
                    //get rating from uid
                    rating.rating = user.rating!!
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            database.addListenerForSingleValueEvent(userListener)
            val uri = commission.imageUri
            Picasso.get().load(uri).into(image)

            description.text = "Description: ${commission.description}"

            medium.text = "Medium: ${commission.medium}"

            time.text = "Estimated Completion Time:${commission.selectedET}"

            min.text = "Minimum Price: ${commission.minPrice}"

            max.text = "Maximum Price: ${commission.maxPrice}"



        }else{
            //Go to another page
        }




    }
}