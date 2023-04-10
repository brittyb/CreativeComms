package com.example.creativecomms.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.creativecomms.R

import com.example.creativecomms.account.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MessageActivity : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid ?: ""
    private var data = ArrayList<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val sendButton = findViewById<Button>(R.id.sendButton)
        val messageText = findViewById<EditText>(R.id.MessageText)

        // get recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.message_recycler_view)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)
        if(intent.extras != null){
            data.clear()
            val chat = intent.extras!!.getSerializable("info") as Chat

            val messagesDatabase = FirebaseDatabase.getInstance().getReference("messages/${chat.chatID}")
            val messageListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(msg in dataSnapshot.children){
                        val currentMsg = msg.getValue(Message::class.java)
                        val userURI = currentMsg?.owner
                        val userDatabase = FirebaseDatabase.getInstance().getReference("users/$userURI")
                        val userListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val user = dataSnapshot.getValue(User::class.java)
                                data.add(Message(
                                    currentMsg!!.message,
                                    user?.profileImageUri!!, currentMsg!!.timestamp))
                                recyclerView.adapter = MessageAdapter(data)
                                recyclerView.scrollToPosition(data.size-1)

                            }
                            override fun onCancelled(error: DatabaseError) {}
                        }
                        userDatabase.addListenerForSingleValueEvent(userListener)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            messagesDatabase.addListenerForSingleValueEvent(messageListener)
            listenForMessages()
        }

    sendButton.setOnClickListener {
        val message = messageText.text.toString()
        val recyclerView = findViewById<RecyclerView>(R.id.message_recycler_view)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)
        if(intent.extras != null && message != "" && message != null) {
            val chat = intent.extras!!.getSerializable("info") as Chat
            val chatID = chat.chatID

            val userDatabase = FirebaseDatabase.getInstance().getReference("users/$uid")
            val userListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    var ref = FirebaseDatabase.getInstance().getReference("messages/$chatID")
                    val id = ref.push().key
                    ref = FirebaseDatabase.getInstance().getReference("messages/$chatID/$id")
                    ref.setValue(Message(message, user?.uid!!, System.currentTimeMillis()/1000))
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
            userDatabase.addListenerForSingleValueEvent(userListener)
            messageText.setText("")

        }

    }

    }
    private fun listenForMessages() {
        Log.d("MessageLog", "listened for message")
        // get recycler view

        if(intent.extras!=null) {
            val chat = intent.extras!!.get("info") as Chat
            val id = chat.chatID
            val ref = FirebaseDatabase.getInstance().getReference("/messages/$id")

            val recyclerView = findViewById<RecyclerView>(R.id.message_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(Message::class.java)
                    val messagesDatabase = FirebaseDatabase.getInstance().getReference("messages/$id")
                    val messagesListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var listOfMessages = ArrayList<Message>()
                          for(msgs in snapshot.children) {
                              val current = msgs.getValue(Message::class.java)
                              listOfMessages.add(current!!)

                            }

                        Log.d("MessageLog", "${listOfMessages.size}")
                            var exists = false
                            for(i in 0..listOfMessages.size-2){
                                Log.d("MessageLog","1: ${listOfMessages.get(i).timestamp}")
                                Log.d("MessageLog","2: ${msg?.timestamp}")
                                if(listOfMessages.get(i).timestamp == msg?.timestamp){
                                    exists = true
                                }
                            }
                            if(!exists){
                                Log.d("MessageLog", "child added")
                                val userURI = msg?.owner
                                val userDatabase = FirebaseDatabase.getInstance().getReference("users/$userURI")
                                val userListener = object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val user = dataSnapshot.getValue(User::class.java)
                                        data.add(
                                            Message(
                                                msg!!.message,
                                                user?.profileImageUri!!, msg!!.timestamp
                                            )
                                        )
                                        Log.d("MessageLog", "recycler")
                                        recyclerView.adapter = MessageAdapter(data)
                                        recyclerView.scrollToPosition(data.size-1)
                                    }
                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                }
                                userDatabase.addListenerForSingleValueEvent(userListener)
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    }
                    messagesDatabase.addListenerForSingleValueEvent(messagesListener)




                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }
    }
}


