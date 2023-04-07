package com.example.creativecomms
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class CompletedCommsAdapter(private val mList: MutableList<CompletedComm>) : RecyclerView.Adapter<CompletedCommsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.completed_comm_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comm = mList[position]
        val viewButton = holder.itemView.findViewById<Button>(R.id.viewButton)
        val uid = FirebaseAuth.getInstance().uid ?: ""

        // sets the text to the textview from our itemHolder class
        holder.titleView.text = comm.commTitle


       viewButton.setOnClickListener {
           val intent = Intent(holder.itemView.context, ViewCompletedActivity::class.java)
           intent.putExtra("CompletedComm", comm)
           holder.itemView.context.startActivity(intent)
       }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleView : TextView = itemView.findViewById(R.id.commTitleText)

    }
}
