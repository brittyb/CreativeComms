package com.example.creativecomms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    lateinit var user : User
    private var numComms : Int = 0
    private var data = ArrayList<ItemsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_account, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        val user_name : TextView = view.findViewById(R.id.profileName)
        val rating: RatingBar = view.findViewById(R.id.profileRating)
        val profilePic: CircleImageView = view.findViewById(R.id.profileImage)
        val editButton : Button = view.findViewById(R.id.btn_editProfile)
        val commButton : Button = view.findViewById(R.id.btn_comm)

        val logoutButton : Button = view.findViewById(R.id.btn_logout)


        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("DatabaseLog", uid)


        //Get current user and display username and rating
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User::class.java)!!
                user_name.text = user.username
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUri.toString())
                //val imageRef = storageRef.child()
                Log.d("DatabaseLog", user.profileImageUri.toString())
                val MAX_SIZE = 1024*1024 * 5
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

                //profilePic.setImageBitmap(bitmap)
                rating.rating = user.rating!!
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }


      database.addListenerForSingleValueEvent(userListener)


        logoutButton.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }



        editButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            activity?.startActivity(intent)
        }

        commButton.setOnClickListener{
            val intent = Intent(activity, CreateCommission::class.java)
            activity?.startActivity(intent)
        }


        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.my_recycler_view)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this.activity)


        // ArrayList of class ItemsViewModel
        //val data = ArrayList<ItemsViewModel>()


//listener for number of comms in firebase
        val commsDatabase = FirebaseDatabase.getInstance().getReference("/Commissions/$uid")
        val commListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Number of Commissions from firebase

                dataSnapshot.children.forEach{
                    val comm = it.getValue(Commission::class.java)
                    data.add(ItemsViewModel(comm?.imageUri.toString(), comm?.title.toString()))
                    val title = comm?.title
                    Log.d("AccountFragLog", "added child to data")
                    Log.d("AccountFragLog", "$title")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        commsDatabase.addListenerForSingleValueEvent(commListener)

        // This will pass the ArrayList to Adapter
        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}