package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreatePostActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var detailsInput: EditText
    private lateinit var createPostButton: Button
    private lateinit var profileIcon: ImageView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createpost)

        titleInput = findViewById(R.id.titleInput)
        detailsInput = findViewById(R.id.detailsInput)
        createPostButton = findViewById(R.id.createPostButton)
        profileIcon = findViewById(R.id.profileIcon)

        createPostButton.setOnClickListener {
            createPost()
        }

//        profileIcon.setOnClickListener {
//            startActivity(Intent(this, UserSettingsActivity::class.java))
//        }
    }

    private fun createPost() {
        val title = titleInput.text.toString().trim()
        val details = detailsInput.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (title.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = database.child("posts").push().key

        if (postId == null) {
            Toast.makeText(this, "Failed to create post ID", Toast.LENGTH_SHORT).show()
            return
        }

        val post = Post(
            id = postId,
            title = title,
            details = details,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )

        database.child("posts").child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // or navigate to post list
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create post: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
