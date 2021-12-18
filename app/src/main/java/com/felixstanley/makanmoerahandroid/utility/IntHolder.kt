package com.felixstanley.makanmoerahandroid.utility

// Holder Class to allow Int value to be passed by reference
data class IntHolder(var value: Int) {
    override fun toString(): String {
        return value.toString()
    }
}
