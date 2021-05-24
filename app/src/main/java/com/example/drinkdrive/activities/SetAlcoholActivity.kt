package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.view.get
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.Math.floor
import kotlin.math.floor

class SetAlcoholActivity : AppCompatActivity() {
    private lateinit var database : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alcohol)

        try {
            database = Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }

        val item=intent.getParcelableExtra<Alcohol>("alcohol")
        val textView=findViewById<TextView>(R.id.alcoholName)
        val imageView=findViewById<ImageView>(R.id.alcoholImage)
        val percent= arrayListOf<Int>()
        for(i in 0 .. 100){
            percent.add(i)
        }
        val capacity=findViewById<EditText>(R.id.editTextNumber)
        val spinnerPercent=findViewById<Spinner>(R.id.spinnerPercent)
        val adapterPercent=ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,percent)
        spinnerPercent.adapter=adapterPercent
        textView.text=item!!.name
        Glide.with(this)
            .load(item.photoURL)
            .into(imageView)
        capacity.hint=item.capacity.toString()
        spinnerPercent.setSelection((item.percent.toInt()))

        findViewById<Button>(R.id.confirm).setOnClickListener{
            GlobalScope.launch {
                var id = database.alcoholDrunkDAO().getLastID() + 1
                var a = AlcoholDrunk(id,
                        textView.text as String,
                        spinnerPercent.selectedItem.toString().toFloat(),
                    100F,
                        //spinnerCapacity.selectedItem.toString().toFloat(),
                        "18-05-2021")
                database.alcoholDrunkDAO().insertAll(a)
            }
            finish()
            true
        }
    }
}