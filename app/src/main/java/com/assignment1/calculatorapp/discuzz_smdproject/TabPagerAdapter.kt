package com.assignment1.calculatorapp.discuzz_smdproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf("Filter by Category", "All Posts", "My Posts")

    override fun getCount(): Int = tabTitles.size

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFilterFragment()
            1 -> AllPostsFragment()
            2 -> MyPostsFragment()
            else -> AllPostsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence = tabTitles[position]
}

