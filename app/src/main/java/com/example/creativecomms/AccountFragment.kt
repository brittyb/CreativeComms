package com.example.creativecomms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.util.Log
import android.widget.Button
import com.bumptech.glide.Glide
import com.example.creativecomms.account.CustomAdapter
import com.example.creativecomms.account.EditProfileActivity
import com.example.creativecomms.account.MainActivity
import com.example.creativecomms.account.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    private lateinit var recyclerView : RecyclerView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Get input fields
        val username : TextView = view.findViewById(R.id.profileName)
        val rating: RatingBar = view.findViewById(R.id.profileRating)
        val profilePic: CircleImageView = view.findViewById(R.id.profileImage)
        val editButton : Button = view.findViewById(R.id.btn_editProfile)
        val commButton : Button = view.findViewById(R.id.btn_message)
        val logoutButton : Button = view.findViewById(R.id.btn_logout)

        //If user is not logged in, go to login page
        if(FirebaseAuth.getInstance().currentUser ==null){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }

        //Database reference to the current user's info
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        Log.d("DatabaseLog", uid)


        //Get info from current user and display
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Get user from snapshot
                user = dataSnapshot.getValue(User::class.java)!!
                //set username to username stored in database
                username.text = user.username
                //load image with glide
                Glide.with(context!!).load(user.profileImageUri).into(profilePic)
                //get rating
                rating.rating = user.rating!!
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        //add the database listener
      database.addListenerForSingleValueEvent(userListener)


        //log the user out
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }


        //go to edit account details page
        editButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            activity?.startActivity(intent)
        }

        //go to add commission page
        commButton.setOnClickListener {
            val intent = Intent(activity, CreateCommission::class.java)
            activity?.startActivity(intent)
        }


        // get recycler view
        recyclerView = view.findViewById(R.id.my_recycler_view)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this.activity)




    //listener for commissions
        val commsDatabase = FirebaseDatabase.getInstance().getReference("/Commissions/$uid")
        val commListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //clear recycler view to repopulate
            data.clear()
                if(dataSnapshot.exists()){
                    //for each commission in the user's commission data list
                    for(commission in dataSnapshot.children){
                        val comm = commission.getValue(Commission::class.java)
                        //add to recycler view
                        data.add(ItemsViewModel(comm?.imageUri.toString(), comm?.title.toString(),
                            comm!!
                        ))
                    }
                    //add adapter to recycler view
                    recyclerView.adapter = CustomAdapter(data)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        //add commission listener
        commsDatabase.addListenerForSingleValueEvent(commListener)



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