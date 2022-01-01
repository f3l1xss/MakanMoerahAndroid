package com.felixstanley.makanmoerahandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
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
    }

    private fun setupBottomNavigationWithNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)
    }
}