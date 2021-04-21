package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.drinkdrive.R

class AddAlcoholActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alcohol)
    }

    fun confirm(view: View) {
        finish()
    }
}