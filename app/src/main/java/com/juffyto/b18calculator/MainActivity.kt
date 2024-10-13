package com.juffyto.b18calculator

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView

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

        bottomNav = findViewById(R.id.bottomNavigation)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
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

    fun navigateToPreselection() {
        loadFragment(PreselectionFragment())
        bottomNav.selectedItemId = R.id.navigation_preselection
    }

    fun navigateToSelection() {
        loadFragment(SelectionFragment())
        bottomNav.selectedItemId = R.id.navigation_selection
    }

    fun navigateToCredits() {
        loadFragment(CreditsFragment())
        bottomNav.selectedItemId = R.id.navigation_credits
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is PreselectionFragment) {
            if (currentFragment.onBackPressed()) {
                return
            }
        }

        when {
            bottomNav.selectedItemId != R.id.navigation_home -> {
                bottomNav.selectedItemId = R.id.navigation_home
            }
            else -> {
                showExitDialog()
            }
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Estás seguro que deseas salir de la aplicación?")
            .setPositiveButton("Sí") { _, _ -> finish() }
            .setNegativeButton("No", null)
            .show()
    }
}