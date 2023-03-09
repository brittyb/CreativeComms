package com.example.creativecomms

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private var filters: SearchActivity.SearchFilters? = null

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

        val bundle = this.arguments
        val searchIsEmpty = bundle?.getBoolean("isEmpty", true)

        if(bundle?.getSerializable("filters") != null){
            filters = bundle?.getSerializable("filters") as SearchActivity.SearchFilters
        }


    //listener for number of comms in firebase
        val commsDatabase = FirebaseDatabase.getInstance().getReference("/Commissions")
        val commListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //clear previous list of results
                data.clear()
                if(dataSnapshot.exists()){
                    var childrenList = ArrayList<Commission>()
                    //get a list of all uids
                    for(userSnapshot in dataSnapshot.children){
                        val commission = userSnapshot
                        //iterate through all commissions in each uid and add to list
                        for(userSnapshot in commission.children){
                            val comm = userSnapshot.getValue(Commission::class.java)
                            childrenList.add(comm!!)
                        }
                        //randomize the list
                        childrenList.shuffle()

                    }
                    //if there are no filters, show a few random posts
                    if(searchIsEmpty == false){

                        //first filter for username
                        //TODO: add rating filter in here too because it uses the user database ref
                        if(filters?.username != ""){
                            var listSize = childrenList.size
                            //for each commission
                            for(i in 0..listSize - 1){
                                var searchedUid = childrenList[i].uid
                                //get all user data
                                val usersDatabase = FirebaseDatabase.getInstance().getReference("/users/$searchedUid")
                                val userListener = object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        //get username from uid
                                            val childUsername = snapshot.child("username").value.toString()
                                        //check if username matches filter
                                            if(childUsername == filters?.username){
                                            }else{
                                                //remove irrelevant results from list
                                                childrenList.removeAt(i)
                                                listSize -=1
                                            }

                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // handle error
                                    }
                                }
                                usersDatabase.addListenerForSingleValueEvent(userListener)
                            }
                        }
                        var newList = ArrayList<Commission>()
                        //filter by title
                        if(filters?.title != ""){
                            var listSize = childrenList.size - 1
                            for(i in 0..listSize){
                                if(childrenList[i].title == filters?.title){
                                    //create a new list for filtered items
                                    newList.add(childrenList[i])
                                }
                            }
                            //set back to childrenList for next filter
                            childrenList=newList
                        }

                        if(filters?.medium != ""){
                            var listSize = childrenList.size - 1
                            for(i in 0..listSize){
                                if(childrenList[i].medium == filters?.medium){
                                    //create a new list for filtered items
                                    newList.add(childrenList[i])
                                }
                            }
                            //set back to childrenList for next filter
                            childrenList=newList
                        }

                        //filter by minimum value
                        if(filters?.min != 0.0){
                            var listSize = childrenList.size - 1
                            for(i in 0..listSize){
                                if(childrenList[i].maxPrice!! >= filters?.min!!){
                                    //create a new list for filtered items
                                    newList.add(childrenList[i])
                                }
                            }
                            //set back to childrenList for next filter
                            childrenList=newList
                        }

                        //filter by maximum value
                        if(filters?.max != 0.0){
                            var listSize = childrenList.size - 1
                            for(i in 0..listSize){
                                if(childrenList[i].maxPrice!! <= filters?.max!!){
                                    //create a new list for filtered items
                                    newList.add(childrenList[i])
                                }
                            }
                            //set back to childrenList for next filter
                            childrenList=newList
                        }

                        //filter by tags
                        if(filters?.tag != ""){
                            var listSize = childrenList.size - 1
                            for(i in 0..listSize){
                                if(childrenList[i].tag1 == filters?.tag || childrenList[i].tag2 == filters?.tag){
                                    //create a new list for filtered items
                                    newList.add(childrenList[i])
                                }
                            }
                            //set back to childrenList for next filter
                            childrenList=newList
                        }

                        //add the data to the recyclerView
                        for(i in 0 .. childrenList.size - 1){
                            data.add(ItemsViewModel(childrenList[i].imageUri.toString(), childrenList[i].title.toString(),
                                childrenList[i]))
                        }
                        recyclerView.adapter = ViewAdapter(data)

                    //for no filters
                    }else{
                        for(i in 0..2){
                            val comm = childrenList[i]
                            //make sure commissions are not from the current user's profile
                            if(comm?.uid != uid){
                                data.add(ItemsViewModel(comm?.imageUri.toString(), comm?.title.toString(), comm))
                            }

                        }
                    }


                    //add data to recyclerview
                    recyclerView.adapter = ViewAdapter(data)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        commsDatabase.addListenerForSingleValueEvent(commListener)



        filtersButton = view.findViewById(R.id.editFiltersButton)

        //go to edit filters page
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