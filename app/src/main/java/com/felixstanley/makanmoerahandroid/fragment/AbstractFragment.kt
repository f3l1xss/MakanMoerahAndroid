package com.felixstanley.makanmoerahandroid.fragment

import androidx.fragment.app.Fragment

abstract class AbstractFragment : Fragment() {

    protected fun getPixelFromDp(dp: Int): Int {
        return ((requireContext().resources.displayMetrics.density * dp).toInt());
    }

}