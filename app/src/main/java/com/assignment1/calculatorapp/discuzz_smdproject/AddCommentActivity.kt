package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class AddCommentActivity : AppCompatActivity() {

    private lateinit var detailsInput: EditText
    private lateinit var createPostButton: Button
    private lateinit var profileIcon: ImageView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        detailsInput = findViewById(R.id.detailsInput)
        createPostButton = findViewById(R.id.createPostButton)
        profileIcon = findViewById(R.id.profileIcon)

        createPostButton.setOnClickListener {
            createComment()
        }
    }

    private fun createComment() {
        val commentText = detailsInput.text.toString().trim()
        val userId = auth.currentUser?.uid
        val postId = intent.getStringExtra("postId")

        if (commentText.isEmpty() || userId == null || postId.isNullOrEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show()
            return
        }

        val commentId = database.child("posts").child(postId).child("comments").push().key ?: return

        val comment = Comment(
            id = commentId,
            postId = postId,
            userId = userId,
            text = commentText,
            timestamp = System.currentTimeMillis()
        )

        // Save comment
        val commentRef = database.child("posts").child(postId).child("comments").child(commentId)
        commentRef.setValue(comment).addOnSuccessListener {
            // Increment comment count in post
            val postRef = database.child("posts").child(postId).child("commentsCount")
            postRef.setValue(ServerValue.increment(1))

            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
