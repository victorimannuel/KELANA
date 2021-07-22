package com.example.kelana.Adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kelana.UI.Detail.DeskripsiFragment
import com.example.kelana.UI.Detail.PeraturanFragment
import com.example.kelana.UI.Detail.ReviewFragment

internal class DetailAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int,
    var idDestinasi : String
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                DeskripsiFragment.newInstance(idDestinasi)
            }
            1 -> {
                PeraturanFragment.newInstance(idDestinasi)
            }
            2 -> {
                ReviewFragment()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}