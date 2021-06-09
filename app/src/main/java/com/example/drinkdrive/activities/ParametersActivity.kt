package com.example.drinkdrive.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.example.drinkdrive.database.Parameters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_parameters.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ParametersActivity : AppCompatActivity() {

    private lateinit var spinnerHeight:Spinner
    private lateinit var spinnerWeight:Spinner
    private lateinit var spinnerCoutry:Spinner
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var shared: SharedPreferences
    private lateinit var male:RadioButton
    private lateinit var female:RadioButton
    private lateinit var database:AppDatabase
    private var user:String?=null
    private var exist= mutableListOf<Parameters>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)
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
        shared = getSharedPreferences("country", Context.MODE_PRIVATE)
        editor = shared.edit()
        user=Firebase.auth.currentUser!!.uid
        exist=database.parameterDAO().getAll(user!!)
        spinnerHeight=findViewById(R.id.spinnerHeight)
        spinnerWeight=findViewById(R.id.spinnerWeight)
        spinnerCoutry=findViewById(R.id.spinnerCoutry)
        male=findViewById(R.id.male)
        female=findViewById(R.id.female)
        val heightArray= arrayListOf<Int>()
        val weightArray= arrayListOf<Int>()
        for(i in 120 .. 220){
            heightArray.add(i)
        }
        for(i in 50 .. 140){
            weightArray.add(i)
        }
        val adapterHeight=
            ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,heightArray)
        spinnerHeight.adapter=adapterHeight
        val adapterWeight=
            ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,weightArray)
        spinnerWeight.adapter=adapterWeight
        val countryAdapter = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.countryPromile))
        spinnerCoutry.adapter = countryAdapter
        spinnerCoutry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> editor.putFloat("country", 0.0f)
                    1 -> editor.putFloat("country", 0.1f)
                    2 -> editor.putFloat("country", 0.2f)
                    3 -> editor.putFloat("country", 0.3f)
                    4 -> editor.putFloat("country", 0.4f)
                    5 -> editor.putFloat("country", 0.5f)
                    6 -> editor.putFloat("country", 0.6f)
                    7 -> editor.putFloat("country", 0.7f)
                    8 -> editor.putFloat("country", 0.8f)
                    9 -> editor.putFloat("country", 0.9f)
                }
                editor.apply()
            }
        }
        if(exist.size>0){
            spinnerHeight.setSelection(exist[0].height.toInt()-120)
            spinnerWeight.setSelection(exist[0].weight.toInt()-50)
            if(exist[0].gender=="male"){
                male.isChecked=true
            }
            else{
                female.isChecked=true
            }
        }

        spinnerHeight.setBackgroundColor(Color.WHITE)
        spinnerWeight.setBackgroundColor(Color.WHITE)
    }

    fun confirm(view: View) {
        val user=Firebase.auth.currentUser!!.uid
        if(male.isChecked){
            GlobalScope.launch {
                if(exist.size==0) {
                    database.parameterDAO().insert(
                        "male",
                        spinnerWeight.selectedItem.toString().toFloat(),
                        spinnerHeight.selectedItem.toString().toFloat(),
                        Firebase.auth.currentUser!!.uid
                    )
                }
                else{
                    database.parameterDAO().set("male",
                        spinnerWeight.selectedItem.toString().toFloat(),
                        spinnerHeight.selectedItem.toString().toFloat(),
                        user)
                }
            }
        }
        else{
            GlobalScope.launch {
                if(exist.size==0) {
                    database.parameterDAO().insert(
                        "female",
                        spinnerWeight.selectedItem.toString().toFloat(),
                        spinnerHeight.selectedItem.toString().toFloat(),
                        Firebase.auth.currentUser!!.uid
                    )
                }
                else{
                    database.parameterDAO().set("female",
                        spinnerWeight.selectedItem.toString().toFloat(),
                        spinnerHeight.selectedItem.toString().toFloat(),
                        user)
                }
            }
        }
        editor.commit()
        finish()
    }


}