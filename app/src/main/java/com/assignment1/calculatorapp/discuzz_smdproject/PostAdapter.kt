package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(private var postList: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.postTitle)
        val contentText: TextView = itemView.findViewById(R.id.postContent)
        val timestampText: TextView = itemView.findViewById(R.id.postTimestamp)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val dislikeCount: TextView = itemView.findViewById(R.id.dislikeCount)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val dislikeIcon: ImageView = itemView.findViewById(R.id.dislikeIcon)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }


override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
    val post = postList[position]
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference

    holder.titleText.text = post.title
    holder.contentText.text = post.details
// Fetch user name
database.child("Users").child(post.userId).get()
    .addOnSuccessListener {
        val fullName = if (post.isAnonymous) {
            "Anonymous"
        } else {
            val firstName = it.child("firstName").value.toString()
            val lastName = it.child("lastName").value.toString()
            "$firstName $lastName"
        }
        holder.itemView.findViewById<TextView>(R.id.postedBy).text = "Posted by: $fullName"
    }
    .addOnFailureListener {
        holder.itemView.findViewById<TextView>(R.id.postedBy).text = "Posted by: Anonymous"
    }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    holder.timestampText.text = sdf.format(Date(post.timestamp))


    // Set comment count
    database.child("posts").child(post.id).child("commentsCount")
        .get()
        .addOnSuccessListener {
            holder.commentCount.text = it.value?.toString() ?: "0"
        }.addOnFailureListener {
            holder.commentCount.text = "0"
        }

    // Navigate to PostDetailsActivity on item click
    holder.itemView.setOnClickListener {
        val context = holder.itemView.context
        val intent = Intent(context, PostDetailsActivity::class.java)
        intent.putExtra("postId", post.id)
        context.startActivity(intent)
    }

    // Set icon state
    val isLiked = post.likedBy?.containsKey(currentUserId) == true
    val isDisliked = post.dislikedBy?.containsKey(currentUserId) == true

    holder.likeIcon.setImageResource(if (isLiked) R.drawable.clike else R.drawable.like)
    holder.dislikeIcon.setImageResource(if (isDisliked) R.drawable.cdislike else R.drawable.dislike)

    holder.likeCount.text = post.likes.toString()
    holder.dislikeCount.text = post.dislikes.toString()

 // Like click
 holder.likeIcon.setOnClickListener {
     val postRef = database.child("posts").child(post.id)
     postRef.runTransaction(object : Transaction.Handler {
         override fun doTransaction(currentData: MutableData): Transaction.Result {
             val p = currentData.getValue(Post::class.java) ?: return Transaction.success(currentData)
             if (p.likedBy == null) p.likedBy = mutableMapOf()
             if (p.dislikedBy == null) p.dislikedBy = mutableMapOf()

             if (p.likedBy.containsKey(currentUserId)) {
                 p.likes -= 1
                 p.likedBy.remove(currentUserId)
             } else {
                 p.likes += 1
                 p.likedBy[currentUserId!!] = true

                 // Remove dislike
                 if (p.dislikedBy.containsKey(currentUserId)) {
                     p.dislikes -= 1
                     p.dislikedBy.remove(currentUserId)
                 }
             }

             currentData.value = p
             return Transaction.success(currentData)
         }

         override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
             if (committed && snapshot != null) {
                 val updatedPost = snapshot.getValue(Post::class.java)
                 if (updatedPost != null) {
                     postList[position] = updatedPost
                     notifyItemChanged(position)
                 }
             }
         }
     })
 }

 // Dislike click
 holder.dislikeIcon.setOnClickListener {
     val postRef = database.child("posts").child(post.id)
     postRef.runTransaction(object : Transaction.Handler {
         override fun doTransaction(currentData: MutableData): Transaction.Result {
             val p = currentData.getValue(Post::class.java) ?: return Transaction.success(currentData)
             if (p.likedBy == null) p.likedBy = mutableMapOf()
             if (p.dislikedBy == null) p.dislikedBy = mutableMapOf()

             if (p.dislikedBy.containsKey(currentUserId)) {
                 p.dislikes -= 1
                 p.dislikedBy.remove(currentUserId)
             } else {
                 p.dislikes += 1
                 p.dislikedBy[currentUserId!!] = true

                 // Remove like
                 if (p.likedBy.containsKey(currentUserId)) {
                     p.likes -= 1
                     p.likedBy.remove(currentUserId)
                 }
             }

             currentData.value = p
             return Transaction.success(currentData)
         }

         override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
             if (committed && snapshot != null) {
                 val updatedPost = snapshot.getValue(Post::class.java)
                 if (updatedPost != null) {
                     postList[position] = updatedPost
                     notifyItemChanged(position)
                 }
             }
         }
     })
 }

    // Comment click
    holder.commentIcon.setOnClickListener {
        val context = holder.itemView.context
        val intent = Intent(context, AddCommentActivity::class.java)
        intent.putExtra("postId", post.id)
        context.startActivity(intent)
    }
}
    override fun getItemCount(): Int = postList.size

    fun updateList(newList: List<Post>) {
        postList.clear()
        postList.addAll(newList)
        notifyDataSetChanged()
    }
}
