package com.juffyto.b18calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_preselection -> {
                    loadFragment(PreselectionFragment())
                    true
                }
                R.id.navigation_selection -> {
                    loadFragment(SelectionFragment())
                    true
                }
                R.id.navigation_credits -> {
                    loadFragment(CreditsFragment())
                    true
                }
                else -> false
            }
        }

        // Cargar el fragmento de inicio por defecto
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}