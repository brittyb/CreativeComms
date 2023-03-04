package com.example.creativecomms

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView
    private var data = ArrayList<ItemsViewModel>()
    private lateinit var filtersButton : Button

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        // getting the recyclerview by its id
        recyclerView = view.findViewById<RecyclerView>(R.id.my_recycler_view)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this.activity)

    //listener for number of comms in firebase
        val commsDatabase = FirebaseDatabase.getInstance().getReference("/Commissions")
        val commListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Number of Commissions from firebase
                data.clear()
                if(dataSnapshot.exists()){
                    val childrenList = ArrayList<Commission>()

                    for(userSnapshot in dataSnapshot.children){

                        val user = userSnapshot
                        for(userSnapshot in user.children){
                            val comm = userSnapshot.getValue(Commission::class.java)
                            childrenList.add(comm!!)
                        }
                        childrenList.shuffle()

                        for(i in 0..2){
                            val comm = childrenList[i]
                            data.add(ItemsViewModel(comm?.imageUri.toString(), comm?.title.toString()))
                        }
                        recyclerView.adapter = ViewAdapter(data)
                    }

                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        commsDatabase.addListenerForSingleValueEvent(commListener)

        filtersButton = view.findViewById(R.id.editFiltersButton)

        filtersButton.setOnClickListener(){
            val intent = Intent(activity, SearchActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}