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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChatsAdapter(private val mList: MutableList<ChatViewModel>) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chats_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("ChatLog", "added to adapter")
        val ChatViewModel = mList[position]


        // sets the image to the imageview from our itemHolder class
        val uri = ChatViewModel.reciever.profileImageUri
        Glide.with(holder.itemView).load(uri).into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ChatViewModel.reciever.username

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MessageActivity::class.java)
            intent.putExtra("info", ChatViewModel.chat)
            holder.itemView.context.startActivity(intent)
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profileImage)
        val textView: TextView = itemView.findViewById(R.id.nameText)
    }
}
