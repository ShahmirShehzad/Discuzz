package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var postTitle: TextView
    private lateinit var postDetails: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private val commentsList = mutableListOf<Comment>()

    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        postTitle = findViewById(R.id.postTitle)
        postDetails = findViewById(R.id.postDetails)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        val addCommentButton = findViewById<Button>(R.id.addCommentButton)

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsAdapter = CommentsAdapter(commentsList)
        commentsRecyclerView.adapter = commentsAdapter

        val postId = intent.getStringExtra("postId") ?: return

        addCommentButton.setOnClickListener {
            val postId = intent.getStringExtra("postId")
            val intent = Intent(this, AddCommentActivity::class.java)
            intent.putExtra("postId", postId)
            startActivity(intent)
        }

        // Fetch post details
        database.child("posts").child(postId).get().addOnSuccessListener { snapshot ->
            val post = snapshot.getValue(Post::class.java)
            post?.let {
                postTitle.text = it.title
                postDetails.text = it.details
            }
        }

        // Fetch comments
        database.child("posts").child(postId).child("comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    commentsList.clear()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        comment?.let { commentsList.add(it) }
                    }
                    commentsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}