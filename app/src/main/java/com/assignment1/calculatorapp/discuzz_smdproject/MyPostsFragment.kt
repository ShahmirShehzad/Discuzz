package com.assignment1.calculatorapp.discuzz_smdproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyPostsFragment : Fragment() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.postsRecyclerView)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Get posts for the current user from Firebase Realtime Database
        fetchUserPosts { posts ->
            // Filter posts to show only the current user's posts
            val myPosts = posts.filter { it.userId == currentUserId }.toMutableList()

            // Set up RecyclerView with the filtered posts
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = PostAdapter(myPosts)
        }

        return view
    }

    // Function to fetch posts from Firebase Realtime Database
    private fun fetchUserPosts(callback: (List<Post>) -> Unit) {
        // Reference to the posts in Firebase Realtime Database
        val postsRef = database.child("posts")

        // Fetch posts from Firebase
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                callback(posts)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(context, "Failed to load posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
