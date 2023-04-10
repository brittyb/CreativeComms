package com.example.creativecomms.chatting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.creativecomms.R
import com.example.creativecomms.account.CustomAdapter
import com.example.creativecomms.account.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView
    private var data = ArrayList<ChatViewModel>()
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.my_recycler_view)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this.activity)
        data.clear()
        //listener for chats
        val chatsDatabase = FirebaseDatabase.getInstance().getReference("/chats/$uid")
        val chatsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    val chat = child.getValue(Chat::class.java)
                    val id = chat?.otherUID
                    val ref = FirebaseDatabase.getInstance().getReference("users/$id")
                    val userListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            Log.d("ChatLog", "added")
                            data.add(ChatViewModel(user!!, chat!!))
                            recyclerView.adapter = ChatsAdapter(data)
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    }
                    ref.addListenerForSingleValueEvent(userListener)

                }

            }


            override fun onCancelled(error: DatabaseError) {

            }
        }
        chatsDatabase.addListenerForSingleValueEvent(chatsListener)
    }
}