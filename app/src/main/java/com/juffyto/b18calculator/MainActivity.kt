package com.juffyto.b18calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Cambiado a false

        // Cargar el fragmento de inicio por defecto
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_preselection -> loadFragment(PreselectionFragment())
                R.id.navigation_selection -> loadFragment(SelectionFragment())
                R.id.navigation_credits -> loadFragment(CreditsFragment())
                else -> false
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    fun navigateToPreselection() = loadFragment(PreselectionFragment())
    fun navigateToSelection() = loadFragment(SelectionFragment())
    fun navigateToCredits() = loadFragment(CreditsFragment())
}