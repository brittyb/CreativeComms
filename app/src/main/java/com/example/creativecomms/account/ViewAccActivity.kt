package com.example.creativecomms.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.creativecomms.Commission
import com.example.creativecomms.ItemsViewModel
import com.example.creativecomms.R
import com.example.creativecomms.chatting.Chat
import com.example.creativecomms.chatting.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ViewAccActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private var data = ArrayList<ItemsViewModel>()
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_account)


        if(intent.extras != null) {
            val extras = intent.extras
            val user = extras?.getSerializable("User") as User
            val username: TextView = findViewById(R.id.profileName)
            val rating: RatingBar = findViewById(R.id.profileRating)
            val profilePic: CircleImageView = findViewById(R.id.profileImage)
            val messageButton: Button = findViewById(R.id.btn_message)

            username.text = user.username
            Glide.with(this).load(user.profileImageUri).into(profilePic)
            rating.rating = user.rating!!


            // get recycler view
            recyclerView = findViewById(R.id.my_recycler_view)

            // this creates a vertical layout Manager
            recyclerView.layoutManager = LinearLayoutManager(this)

            //listener for commissions
            val commsDatabase =
                FirebaseDatabase.getInstance().getReference("/Commissions/${user.uid}")
            val commListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //clear recycler view to repopulate
                    data.clear()
                    if (dataSnapshot.exists()) {
                        //for each commission in the user's commission data list
                        for (commission in dataSnapshot.children) {
                            val comm = commission.getValue(Commission::class.java)
                            //add to recycler view
                            data.add(
                                ItemsViewModel(
                                    comm?.imageUri.toString(), comm?.title.toString(),
                                    comm!!
                                )
                            )
                        }
                        //add adapter to recycler view
                        recyclerView.adapter = ViewAccAdapter(data)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            //add commission listener
            commsDatabase.addListenerForSingleValueEvent(commListener)

            messageButton.setOnClickListener {
                //listener for chats
                val chatsDatabase = FirebaseDatabase.getInstance().getReference("/chats/$uid")
                val chatsListener = object : ValueEventListener {


                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val extras = intent.extras
                        val userReq = extras?.getSerializable("User") as User
                        var exists = false
                        for(child in dataSnapshot.children){

                            val chat = child.getValue(Chat::class.java)
                            if(chat?.otherUID == userReq.uid){
                                exists = true
                            }
                        }

                        if(exists == false){

                            val newChatRef = FirebaseDatabase.getInstance().getReference("/chats")
                            val id = newChatRef.push().key
                            val newChatRef2 = FirebaseDatabase.getInstance().getReference("/chats/${userReq.uid}/$id")
                            var chat = Chat(userReq.uid.toString(), uid, id.toString())
                            newChatRef2.setValue(chat)
                            val newChatRef3 = FirebaseDatabase.getInstance().getReference("/chats/$uid/$id")
                            chat = Chat(uid, userReq.uid.toString(), id.toString())
                            newChatRef3.setValue(chat)
                            val messageIntent = Intent(applicationContext, MessageActivity::class.java)
                            messageIntent.putExtra("info", chat)
                            startActivity(messageIntent)
                            val newChatRef4 = FirebaseDatabase.getInstance().getReference("/messages")
                            newChatRef4.child("$id").push().setValue("")

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                }
                chatsDatabase.addListenerForSingleValueEvent(chatsListener)
            }
        }

    }

}
