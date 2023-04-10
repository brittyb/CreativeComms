package com.example.creativecomms.account
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.creativecomms.ItemsViewModel
import com.example.creativecomms.R
import com.example.creativecomms.ViewCommissionActivity

class ViewAccAdapter(private val mList: MutableList<ItemsViewModel>) : RecyclerView.Adapter<ViewAccAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_commission_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]


        // sets the image to the imageview from our itemHolder class
        val uri = ItemsViewModel.image
        Glide.with(holder.itemView).load(uri).into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

       holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView.context, ViewCommissionActivity::class.java)
            intent.putExtra("Commission", ItemsViewModel.comm)
            holder.itemView.context.startActivity(intent)
        }




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
