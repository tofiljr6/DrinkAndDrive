package com.example.drinkdrive.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drinkdrive.R

class AddAlcoholActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alcohol)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title="Add alcohol"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}