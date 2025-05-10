package com.assignment1.calculatorapp.discuzz_smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Profile icon click → go to User Settings
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            startActivity(Intent(this, UserSettingsActivity::class.java))
        }

        // Setup ViewPager with fragments
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val adapter = TabPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        // FAB to create new post
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }
}
