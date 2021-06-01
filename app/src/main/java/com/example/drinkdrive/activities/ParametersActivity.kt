package com.example.drinkdrive.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.example.drinkdrive.database.Parameters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ParametersActivity : AppCompatActivity() {

    private lateinit var spinnerHeight:Spinner
    private lateinit var spinnerWeight:Spinner
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
        user=Firebase.auth.currentUser!!.uid
        exist=database.parameterDAO().getAll(user!!)
        spinnerHeight=findViewById(R.id.spinnerHeight)
        spinnerWeight=findViewById(R.id.spinnerWeight)
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
        finish()
    }


}