package com.example.drinkdrive.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import java.lang.Exception

class SettingsActivity : AppCompatActivity() {

    private lateinit var database:AppDatabase
    private lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        //findViewById<SwitchCompat>(R.id.notify).isChecked = shared.getString("notifications", null) == "true"

        try {
            database = Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }
    }

    fun deleteHistory(view: View) {
        database.alcoholDrunkDAO().deleteAll()
    }

    fun notifications(view: View) {
        val editor=shared.edit()
        editor.putString("notifications", (view as SwitchCompat).isChecked.toString())
        editor.commit()
    }
}
