package com.example.creativecomms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.creativecomms.account.User
import com.example.creativecomms.account.ViewAccActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ViewCommissionActivity : AppCompatActivity() {
    lateinit var user : User
    private val uid = FirebaseAuth.getInstance().uid ?: ""
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

        val requestButton = findViewById<Button>(R.id.requestButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val homeButton = findViewById<ImageView>(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
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
                    Glide.with(applicationContext).load(user.profileImageUri).into(pfp)
                    //get rating from uid
                    rating.rating = user.rating!!
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            database.addListenerForSingleValueEvent(userListener)
            val uri = commission.imageUri
            Glide.with(applicationContext).load(uri).into(image)

            description.text = "Description: ${commission.description}"

            medium.text = "Medium: ${commission.medium}"

            time.text = "Estimated Completion Time: ${commission.selectedET} days"

            min.text = "Minimum Price: ${commission.minPrice}"

            max.text = "Maximum Price: ${commission.maxPrice}"


            requestButton.setOnClickListener {
                if(uid != commission.uid){
                    val intent = Intent(this, RequestActivity::class.java)
                    intent.putExtra("Title", commission.title)
                    intent.putExtra("commUID", commission.uid)
                    intent.putExtra("ID", commission.commID)
                    startActivity(intent)
                }

            }

            saveButton.setOnClickListener {
                if(uid != commission.uid){
                    val savesData = FirebaseDatabase.getInstance().getReference("Saves/$uid")
                    val savesListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val newSaves = FirebaseDatabase.getInstance().getReference("Saves/$uid/${commission.commID}")
                            var alreadyAdded = false
                            for(save in dataSnapshot.children){
                                val saveID = save.value
                                if(saveID == commission.commID){
                                    alreadyAdded = true

                                }
                            }
                            if(!alreadyAdded){
                                newSaves.setValue(commission.commID)
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    savesData.addListenerForSingleValueEvent(savesListener)
                }

            }

        }else{
            //Go to another page
        }

        pfp.setOnClickListener{
            val commission = intent.getSerializableExtra("Commission") as Commission
            val database = FirebaseDatabase.getInstance().getReference("/users/${commission.uid}")

            val userReqListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot!=null){
                        val userReq = dataSnapshot.getValue(User::class.java)!!
                        val intentReq = Intent(applicationContext, ViewAccActivity::class.java)
                        intentReq.putExtra("User", userReq)
                        startActivity(intentReq)
                    }

                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            database.addListenerForSingleValueEvent(userReqListener)


        }


    }
}