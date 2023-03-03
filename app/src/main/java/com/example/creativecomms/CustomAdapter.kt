package com.example.creativecomms
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.creativecomms.ItemsViewModel

class CustomAdapter(private val mList: MutableList<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profilegrid_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        val button_del = holder.itemView.findViewById<Button>(R.id.delete_button)
        val button_ed = holder.itemView.findViewById<Button>(R.id.edit_button)

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageURI(Uri.parse(ItemsViewModel.image))

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

        /*
        button_del.setOnClickListener{
            removeItem(position)
        }

         */
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    private fun removeItem(position: Int){
        //mList[0].comms.removeAt(position)
        mList.removeAt(position)
        notifyDataSetChanged()
    }

    /*public fun getMutableList(position : Int) : MutableList<Commission>{
        //return mList[0].comms
    }
    */

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.commPic)
        val textView: TextView = itemView.findViewById(R.id.commTitle)
    }
}
