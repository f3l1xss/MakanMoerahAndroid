package com.felixstanley.makanmoerahandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.felixstanley.makanmoerahandroid.constants.Constants
import com.felixstanley.makanmoerahandroid.network.cookie.CookieJarImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.Cookie
import okhttp3.HttpUrl

class MainActivity : AppCompatActivity() {
    companion object {
        private lateinit var instance: MainActivity

        fun getInstance(): MainActivity {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Intent Data and parse cookie if request is coming from OAuth2 Login Redirection
        handleCookieIntentData(intent)

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

    private fun handleCookieIntentData(intent: Intent) {
        // Redirection from OAuth2 Login will result in ACTION_VIEW
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let { intentData ->
                val cookies =
                    intentData.getQueryParameter(Constants.OAUTH2_REDIRECTION_URL_COOKIE_VALUE_QUERY_PARAMETER_NAME)
                val cookieValueList =
                    cookies?.split(Constants.OAUTH2_REDIRECTION_URL_COOKIE_VALUE_DELIMITER)
                cookieValueList?.let {
                    // For Each Cookie Value Found , insert it into DB
                    for (cookieValue in it.listIterator()) {
                        val cookie = Cookie.parse(
                            HttpUrl.parse(intentData.toString()),
                            cookieValue
                        )
                        cookie?.let {
                            CookieJarImpl.insertNewCookie(it)
                        }
                    }
                    // Finally, Refresh our Cookies Cache with latest value from DB
                    CookieJarImpl.retrieveLatestCookiesFromDb()
                }
            }
        }
    }

}