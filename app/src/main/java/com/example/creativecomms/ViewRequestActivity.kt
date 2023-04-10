package com.example.creativecomms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.creativecomms.account.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ViewRequestActivity : AppCompatActivity() {
    private lateinit var profilePic : CircleImageView
    private lateinit var username : TextView
    private lateinit var rating : RatingBar

    lateinit var user : User
    lateinit var comm : Commission
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_request)
        profilePic = findViewById<CircleImageView>(R.id.profileImage)
        username = findViewById<TextView>(R.id.usernameText)
        rating = findViewById<RatingBar>(R.id.ratingBar)
        val title = findViewById<TextView>(R.id.commTitleText)
        val info = findViewById<TextView>(R.id.infoText)
        val imageRef = findViewById<ImageView>(R.id.referenceImage)

        val homeButton = findViewById<ImageView>(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val extras = intent
        if(extras != null){
            val req = extras.getSerializableExtra("Request") as Request
            val reqID = req.requesterUID
            info.text = req.description
            Glide.with(applicationContext).load(req.imageUri).into(imageRef)
            val database = FirebaseDatabase.getInstance().getReference("/users/$reqID")
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get User from firebase
                    user = dataSnapshot.getValue(User::class.java)!!
                    username.text=user.username.toString()


                    Glide.with(applicationContext).load(user.profileImageUri).into(profilePic)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            database.addListenerForSingleValueEvent(userListener)

            val commID = req.commID
            val artistID = req.artistUID
            val commDatabase = FirebaseDatabase.getInstance().getReference("/Commissions/$artistID")
            val commListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Comm from firebase
                    for(commission in dataSnapshot.children){
                        comm = commission.getValue(Commission::class.java)!!
                        title.text = comm.title
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            commDatabase.addListenerForSingleValueEvent(commListener)
        }

    }
}