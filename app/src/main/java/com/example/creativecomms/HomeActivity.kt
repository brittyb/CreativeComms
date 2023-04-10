package com.example.creativecomms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.creativecomms.chatting.ChatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // define your fragments here
        val fragment1: Fragment = HomeArtistFragment()
        val fragment2: Fragment = SavedFragment()
        val fragment3: Fragment = ChatFragment()
        val fragment4: Fragment = AccountFragment()
        val fragment5: Fragment = SearchFragment()



        val fragmentManager: FragmentManager = supportFragmentManager

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.page_1 -> fragment = fragment1
                R.id.page_2 -> fragment = fragment2
                R.id.page_3 -> fragment = fragment3
                R.id.page_4 -> fragment = fragment4
                R.id.page_5 -> fragment = fragment5
            }
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit()
            true

        }
        val mBundle = Bundle()
        mBundle.putBoolean("isEmpty", true)
        fragment5.arguments = mBundle
        val extras = intent
        if(extras!= null){
            val isSearch = extras.getBooleanExtra("search", false)
            if(isSearch == true){
                val isEmpty = extras.getBooleanExtra("empty", true)
                val filters = extras.getSerializableExtra("filters")

                mBundle.putSerializable("filters", filters)
                mBundle.putBoolean("isEmpty", isEmpty)
                fragment5.arguments = mBundle
                bottomNavigationView.selectedItemId = R.id.page_5
            }else{
                bottomNavigationView.selectedItemId = R.id.page_1
            }
        }else{
            // Set default selection
            bottomNavigationView.selectedItemId = R.id.page_1
        }

    }

}
