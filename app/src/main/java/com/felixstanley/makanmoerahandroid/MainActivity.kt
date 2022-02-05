package com.felixstanley.makanmoerahandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    companion object {
        private lateinit var instance: MainActivity

        fun getInstance(): MainActivity {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup Bottom Navigation With Nav Controller
        setupBottomNavigationWithNavController()

        // Setup App Bar / Action Bar
        setupActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        // Enable Navigation Up Functionality
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    private fun setupBottomNavigationWithNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)
    }

    private fun setupActionBar() {
        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // Hide Title Text at Action Bar
        supportActionBar!!.setDisplayShowTitleEnabled(false);
    }

}