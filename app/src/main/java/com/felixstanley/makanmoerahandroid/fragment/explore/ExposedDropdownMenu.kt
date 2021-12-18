package com.felixstanley.makanmoerahandroid.fragment.explore

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView

/*
This Class exists to avoid bug in which screen rotation
will trigger filtering on exposed dropdown menu

https://github.com/material-components/material-components-android/issues/1464
*/
class ExposedDropdownMenu @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialAutoCompleteTextView(context, attrs) {

    override fun getFreezesText(): Boolean {
        return false
    }
}