package com.example.creativecomms.chatting
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.creativecomms.EditCommissionActivity
import com.example.creativecomms.ItemsViewModel
import com.example.creativecomms.R
import com.example.creativecomms.ViewCommissionActivity
import com.example.creativecomms.account.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdapter(private val mList: MutableList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val Message = mList[position]
        holder.textView.text = Message.message
        Log.d("MessageAdaptLog", Message.message)
        // sets the image to the imageview from our itemHolder class
        val uri = Message.owner
        Glide.with(holder.itemView).load(uri).into(holder.imageView)




    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profileImage)
        val textView: TextView = itemView.findViewById(R.id.messageText)
    }
}
