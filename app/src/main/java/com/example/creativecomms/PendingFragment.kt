package com.example.creativecomms

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PendingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var data = mutableListOf<PendingViewModel>()
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private lateinit var recyclerView : RecyclerView
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
        return inflater.inflate(R.layout.fragment_pending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById<RecyclerView>(R.id.my_recycler_view)
        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this.activity)

        val requestsDatabase = FirebaseDatabase.getInstance().getReference("/Requests/$uid")
        val reqListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Number of Commissions from firebase
                data.clear()
                if(dataSnapshot.exists()){
                    for(userSnapshot in dataSnapshot.children){
                        val req = userSnapshot.getValue(Request::class.java)
                        Log.d("PendingLog", "getting req")
                        var status = ""
                        if(req?.pending == true){
                            status = "Waiting for request to be accepted"
                        }else if(req?.completed == false){
                            status = "Commission not yet completed"
                        }else if(req?.paid == false){
                            status = "Waiting for payment"
                        }else{
                            status = "Commission completed and paid"
                        }
                        val commID = req?.commID
                        val artistID = req?.artistUID
                        val commissionDatabase = FirebaseDatabase.getInstance().getReference("/Commissions/$artistID")
                        val commListener = object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                Log.d("PendingLog", "Added commListener")
                                if(snapshot.exists()){
                                    Log.d("PendingLog", "snapshot exists")

                                    for(item in snapshot.children){
                                        Log.d("PendingLog", "getting comm")
                                        val comm = item.getValue(Commission::class.java)
                                        if(comm?.commID.toString() == commID.toString()){
                                            Log.d("PendingLog", "added")
                                            data.add(
                                                PendingViewModel(comm?.title.toString(),
                                                    req!!, status)
                                            )
                                        }
                                    }
                                    Log.d("PendingLog", "Attaching Adapter")
                                    recyclerView.adapter = PendingAdapter(data)
                                }

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // handle error
                            }
                        }
                        commissionDatabase.addListenerForSingleValueEvent(commListener)
                    }

                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        requestsDatabase.addListenerForSingleValueEvent(reqListener)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PendingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PendingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}