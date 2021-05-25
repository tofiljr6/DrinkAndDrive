package com.example.drinkdrive.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import com.example.drinkdrive.R

class ParametersActivity : AppCompatActivity() {

    private lateinit var spinnerHeight:Spinner
    private lateinit var spinnerWeight:Spinner
    private lateinit var male:RadioButton
    private lateinit var female:RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)
        spinnerHeight=findViewById(R.id.spinnerHeight)
        spinnerWeight=findViewById(R.id.spinnerWeight)
        male=findViewById(R.id.male)
        female=findViewById(R.id.female)
        val heightArray= arrayListOf<Int>()
        val weightArray= arrayListOf<Int>()
        for(i in 120 until 200){
            heightArray.add(i)
        }
        for(i in 50 until 120){
            weightArray.add(i)
        }
        val adapterHeight=
            ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,heightArray)
        spinnerHeight.adapter=adapterHeight
        val adapterWeight=
            ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,weightArray)
        spinnerWeight.adapter=adapterWeight
    }

    fun confirm(view: View) {
        val shared=getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor=shared.edit()
        if(male.isChecked){
            editor.putString("gender","male")
        }
        else{
            editor.putString("gender","female")
        }
        editor.putInt("height",spinnerHeight.selectedItem as Int)
        editor.putInt("weight",spinnerWeight.selectedItem as Int)
        editor.commit()
        finish()
    }


}