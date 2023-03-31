package com.example.creativecomms
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class CurrentCommsAdapter(private val mList: MutableList<PendingViewModel>) : RecyclerView.Adapter<CurrentCommsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.current_comm_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pendingViewModel = mList[position]
        val uploadButton = holder.itemView.findViewById<Button>(R.id.uploadButton)
        val uid = FirebaseAuth.getInstance().uid ?: ""

        // sets the text to the textview from our itemHolder class
        holder.titleView.text = pendingViewModel.title
        holder.statusView.text = pendingViewModel.status


       uploadButton.setOnClickListener {

       }

        holder.titleView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewRequestActivity::class.java)
            intent.putExtra("Request", pendingViewModel.request)
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

    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleView : TextView = itemView.findViewById(R.id.commTitleText)
        val statusView : TextView = itemView.findViewById(R.id.statusText)
    }
}
