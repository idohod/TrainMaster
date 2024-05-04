package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

class SignalGenerator private constructor(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SignalGenerator? = null

        @JvmStatic
        fun init(context: Context) {
            if (instance == null) {
                instance = SignalGenerator(context)
            }
        }

        @JvmStatic
        fun getInstance(): SignalGenerator {
            return instance ?: throw IllegalStateException("com.example.finalproject.SignalGenerator is not initialized. Call init() first.")
        }
    }

    fun toast(text: String, length: Int) {
        Toast.makeText(context, text, length).show()
    }
}
