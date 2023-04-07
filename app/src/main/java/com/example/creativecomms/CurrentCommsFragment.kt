package com.example.creativecomms

import android.os.Bundle
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
 * Use the [CurrentCommsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentCommsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_current_comms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.my_recycler_view)
        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this.activity)

        val requestsDatabase = FirebaseDatabase.getInstance().getReference("/ArtistRequests/$uid")
        val reqListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                data.clear()
                if(dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val req = userSnapshot.getValue(Request::class.java)
                        val commID = req?.commID
                        val requesterID = req?.requesterUID
                        var commTitle = ""
                        var status = ""

                        if(req?.pending == false) {
                            val commDatabase = FirebaseDatabase.getInstance().getReference("/Commissions/$uid")
                            val commListener = object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (commission in snapshot.children) {
                                        val comm = commission.getValue(Commission::class.java)
                                        if (comm?.commID == commID) {
                                            commTitle = comm?.title.toString()
                                            if(req?.paid == true) {
                                                if(req?.completed == false) {
                                                    status ="Commission paid for but not completed"
                                                }else{
                                                    val ref = FirebaseDatabase.getInstance().getReference("ArtistRequests/$uid/${req.reqID}")
                                                    ref.removeValue()
                                                }
                                            }else{
                                                if(req?.completed == false) {
                                                    //status=getString(R.string.not_completed_or_paid_com)
                                                    status = "Commission not completed and not paid"
                                                }else{
                                                    status="Commission completed but not paid for"
                                                }
                                            }
                                            data.add(PendingViewModel(commTitle, req!!, status))
                                        }
                                    }
                                    recyclerView.adapter = CurrentCommsAdapter(data)

                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // handle error
                                }
                            }
                            commDatabase.addListenerForSingleValueEvent(commListener)
                        }

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
         * @return A new instance of fragment CurrentCommsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CurrentCommsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}