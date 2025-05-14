package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.SwitchCompat

class CreatePostActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var detailsInput: EditText
    private lateinit var createPostButton: Button
    private lateinit var profileIcon: ImageView
    private lateinit var categorySpinner: Spinner

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

private lateinit var anonymousSwitch: SwitchCompat


override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_createpost)

    // Initialize UI components
    titleInput = findViewById(R.id.titleInput)
    detailsInput = findViewById(R.id.detailsInput)
    categorySpinner = findViewById(R.id.categorySpinner)
    createPostButton = findViewById(R.id.createPostButton)
    anonymousSwitch = findViewById(R.id.anonymousSwitch)

    // Populate Spinner with tags
            val categories = listOf("All", "Gaming", "Fitness", "Technology", "Education", "Others")

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    categorySpinner.adapter = adapter

    // Set the click listener
    createPostButton.setOnClickListener {
        createPost()
    }
    anonymousSwitch.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            Toast.makeText(this, "Anonymous mode enabled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Anonymous mode disabled", Toast.LENGTH_SHORT).show()
        }
    }
}
private fun createPost() {
    val title = titleInput.text.toString().trim()
    val details = detailsInput.text.toString().trim()
    val selectedCategory = categorySpinner.selectedItem?.toString() ?: ""

    if (title.isEmpty() || details.isEmpty() || selectedCategory.isEmpty() || selectedCategory == "Select Category") {
        Toast.makeText(this, "Please fill in all fields and select a valid category", Toast.LENGTH_SHORT).show()
        return
    }

    val userId = auth.currentUser?.uid
    if (userId == null) {
        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        return
    }

    val postId = database.child("posts").push().key ?: return
    val isAnonymous = anonymousSwitch.isChecked

    val post = Post(
        id = postId,
        title = title,
        details = details,
        userId = userId,
        category = selectedCategory,
        timestamp = System.currentTimeMillis(),
        isAnonymous = isAnonymous
    )

    database.child("posts").child(postId).setValue(post)
        .addOnSuccessListener {
            Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        .addOnFailureListener {
            Toast.makeText(this, "Failed to create post: ${it.message}", Toast.LENGTH_LONG).show()
        }
}

}
