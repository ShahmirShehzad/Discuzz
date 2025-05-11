package com.assignment1.calculatorapp.discuzz_smdproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class CategoryFilterFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var searchView: SearchView
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()

    private val database = FirebaseDatabase.getInstance().reference.child("posts")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        categorySpinner = view.findViewById(R.id.categorySpinner)
        searchView = view.findViewById(R.id.searchView)
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView)

        adapter = PostAdapter(mutableListOf())
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        postsRecyclerView.adapter = adapter

        val categories = listOf("All", "Gaming", "Fitness", "Technology", "Education", "Others")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                fetchPosts(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = postList.filter {
                    it.title.contains(newText ?: "", ignoreCase = true) ||
                            it.details.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    private fun fetchPosts(category: String) {
        database.get().addOnSuccessListener { snapshot ->
            postList.clear()
            for (child in snapshot.children) {
                val post = child.getValue(Post::class.java)
                if (post != null && (category == "All" || post.category == category)) {
                    postList.add(post)
                }
            }
            adapter.updateList(postList)
        }
    }
}
