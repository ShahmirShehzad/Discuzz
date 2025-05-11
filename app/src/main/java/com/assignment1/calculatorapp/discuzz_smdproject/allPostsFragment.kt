package com.assignment1.calculatorapp.discuzz_smdproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AllPostsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_posts, container, false)
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Fetch all posts from Firebase Realtime Database
        fetchAllPosts { posts ->
            allPosts = posts.toMutableList()
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = PostAdapter(allPosts)
            recyclerView.adapter = adapter
        }

        // Set up search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })

        return view
    }

    private fun fetchAllPosts(callback: (List<Post>) -> Unit) {
        val postsRef = database.child("posts")
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
                Toast.makeText(context, "Failed to load posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterPosts(query: String?) {
        val filteredPosts = if (query.isNullOrEmpty()) {
            allPosts
        } else {
            allPosts.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.details.contains(query, ignoreCase = true)
            }
        }
        adapter = PostAdapter(filteredPosts)
        recyclerView.adapter = adapter
    }
}