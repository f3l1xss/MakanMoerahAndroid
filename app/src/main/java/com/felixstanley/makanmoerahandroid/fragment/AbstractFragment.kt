package com.felixstanley.makanmoerahandroid.fragment

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment


abstract class AbstractFragment : Fragment() {

    protected fun getPixelFromDp(dp: Int): Int {
        return ((requireContext().resources.displayMetrics.density * dp).toInt());
    }

    protected fun validateEmailString(email: String?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    protected fun reloadCurrentFragment() {
        parentFragmentManager.beginTransaction().detach(this).commit()
        parentFragmentManager.beginTransaction().attach(this).commit()
    }

    // Different ways to serve web-based content
    // https://developer.android.com/guide/webapps
    // WebView does not support serving Google OAuth2 Authorization Endpoint anymore
    // https://developers.googleblog.com/2021/06/upcoming-security-changes-to-googles-oauth-2.0-authorization-endpoint.html

    protected fun callBrowserIntent(url: String) {
        // Call Browser Intent with Data as param Url
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    protected fun callCustomTabsIntent(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

}