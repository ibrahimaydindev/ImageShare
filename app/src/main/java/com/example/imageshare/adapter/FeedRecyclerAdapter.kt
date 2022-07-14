package com.example.imageshare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imageshare.R
import com.example.imageshare.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class feedRecyclerAdapter(val postList:ArrayList<Post>) : RecyclerView.Adapter<feedRecyclerAdapter.postHolder>() {

    class postHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postHolder {
    val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.recycler_row,parent,false)
        return postHolder(view)
    }

    override fun onBindViewHolder(holder: postHolder, position: Int) {
        holder.itemView.recycler_row_userEmail.text=postList[position].userEmail
        holder.itemView.recyler_row_userComment.text=postList[position].userComment
        Picasso.get().load(postList[position].imageUrl).into(holder.itemView.recycler_row_imageview)

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}