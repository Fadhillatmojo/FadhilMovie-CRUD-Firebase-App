package com.example.uas_papb_2023.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.uas_papb_2023.CrudFragment
import com.example.uas_papb_2023.MainAdminActivity
import com.example.uas_papb_2023.ReadAdminFragment

class TabAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CrudFragment()
            1 -> ReadAdminFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "CRUD Movie"
            1 -> "List Movie"
            else -> null
        }
    }
}