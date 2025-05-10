package com.assignment1.calculatorapp.discuzz_smdproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AllPostsFragment : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.postsRecyclerView)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Fetch all posts from Firebase Realtime Database
        fetchAllPosts { posts ->
            // Set up RecyclerView with the posts from Firebase
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = PostAdapter(posts)
        }

        return view
    }

    // Function to fetch all posts from Firebase Realtime Database
    private fun fetchAllPosts(callback: (List<Post>) -> Unit) {
        // Reference to the posts in Firebase Realtime Database
        val postsRef = database.child("posts")

        // Fetch all posts from Firebase
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
