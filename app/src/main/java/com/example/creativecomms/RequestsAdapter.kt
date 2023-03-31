package com.example.creativecomms
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RequestsAdapter(private val mList: MutableList<RequestsViewModel>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.requests_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val RequestsViewModel = mList[position]
        val acceptButton = holder.itemView.findViewById<Button>(R.id.uploadButton)
        val declineButton = holder.itemView.findViewById<Button>(R.id.declineButton)
        val uid = FirebaseAuth.getInstance().uid ?: ""

        // sets the text to the textview from our itemHolder class
        holder.titleView.text = RequestsViewModel.title
        holder.userView.text = RequestsViewModel.reqUsername


        acceptButton.setOnClickListener(){
            //Get request id
            val reqID = RequestsViewModel.request.reqID
            //Get id of the requester
            val requesterUID = RequestsViewModel.request.requesterUID
            //change pending to false to notify that request was accepted
            var ref = FirebaseDatabase.getInstance().getReference("Requests/$requesterUID/$reqID/pending")
            ref.setValue(false)
            ref = FirebaseDatabase.getInstance().getReference("ArtistRequests/$uid/$reqID/pending")
            ref.setValue(false)
            //remove from adapter
            removeItem(position)

        }

        declineButton.setOnClickListener {
            //Get request id
            val reqID = RequestsViewModel.request.reqID
            //Get id of the requester
            val requesterUID = RequestsViewModel.request.requesterUID
            //delete the request completely
            var ref = FirebaseDatabase.getInstance().getReference("Requests/$requesterUID/$reqID")
            ref.removeValue()
            ref = FirebaseDatabase.getInstance().getReference("ArtistRequests/$uid/$reqID")
            ref.removeValue()
            //remove from adapter
            removeItem(position)
        }

        holder.titleView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ViewRequestActivity::class.java)
            intent.putExtra("Request", RequestsViewModel.request)
            holder.itemView.context.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    private fun removeItem(position: Int){
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleView : TextView = itemView.findViewById(R.id.commTitleText)
        val userView : TextView = itemView.findViewById(R.id.requestFromText)
    }
}
