package com.example.creativecomms
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val payButton = holder.itemView.findViewById<Button>(R.id.acceptButton)


        // sets the text to the textview from our itemHolder class
        holder.titleView.text = RequestsViewModel.title
        holder.userView.text = RequestsViewModel.reqUsername


        payButton.setOnClickListener(){

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



    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleView : TextView = itemView.findViewById(R.id.commTitleText)
        val userView : TextView = itemView.findViewById(R.id.requestFromText)
    }
}
