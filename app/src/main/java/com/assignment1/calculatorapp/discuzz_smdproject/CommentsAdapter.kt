package com.assignment1.calculatorapp.discuzz_smdproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter(private val commentsList: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentTimestamp: TextView = itemView.findViewById(R.id.commentTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.commentText.text = comment.text
        holder.commentTimestamp.text = java.text.DateFormat.getDateTimeInstance()
            .format(java.util.Date(comment.timestamp))
    }

    override fun getItemCount(): Int = commentsList.size
}