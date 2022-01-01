package com.felixstanley.makanmoerahandroid.fragment

import android.util.Patterns
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

}