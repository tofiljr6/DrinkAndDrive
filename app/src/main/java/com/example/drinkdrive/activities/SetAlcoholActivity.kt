package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.example.drinkdrive.R
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

class SetAlcoholActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alcohol)
        val item=intent.getParcelableExtra<Alcohol>("alcohol")
        val textView=findViewById<TextView>(R.id.alcoholName)
        val imageView=findViewById<ImageView>(R.id.alcoholImage)
        findViewById<Button>(R.id.confirm).setOnClickListener{
            //todo
            finish()
            true
        }
        val mapOfCapacity= mapOf(20 to 0,30 to 1,40 to 2,80 to 3,100 to 4,200 to 5,300 to 6,500 to 7,500 to 8,700 to 9,1000 to 10)
        val capacity=mapOfCapacity.keys.toTypedArray()
        val percent= arrayListOf<Int>()
        for(i in 0 .. 100){
            percent.add(i)
        }
        val spinnerCapacity=findViewById<Spinner>(R.id.spinnerCapacity)
        val spinnerPercent=findViewById<Spinner>(R.id.spinnerPercent)
        val adapterCapacity=ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,capacity)
        val adapterPercent=ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,percent)
        spinnerCapacity.adapter=adapterCapacity
        spinnerPercent.adapter=adapterPercent
        textView.text=item!!.name
        Glide.with(this)
            .load(item.photoURL)
            .into(imageView)
        spinnerCapacity.setSelection(mapOfCapacity[item.capacity]!! -1)
        spinnerPercent.setSelection(item.percent.toInt()-1)
    }


}