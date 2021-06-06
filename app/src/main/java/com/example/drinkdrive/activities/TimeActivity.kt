package com.example.drinkdrive.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_time.*
import www.sanju.motiontoast.MotionToast
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class TimeActivity: AppCompatActivity() {


    private lateinit var button: Button
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)
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
        button = findViewById(R.id.buttonTime)
        button.setOnClickListener { timePicker(it) }
    }

    fun timePicker(view: View) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            buttonTime.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun confirm(view: View) {
        val time=button.text
        val ins=findViewById<TextView>(R.id.instructions)
        val params= database.parameterDAO().getAll(Firebase.auth.currentUser!!.uid)
        if(params.size>0) {
            if (button.text != "Time") {
                val delta = time.substring(0, 2).toInt() - LocalTime.now().hour
                var burn = 0
                if (params[0].gender == "male") {
                    burn = 12
                    Log.v("heh", delta.toString())
                } else {
                    burn = 10
                }
                val promile = (delta * burn)/100.toFloat()
                //dokonczyc
                ins.text = promile.toString()
            }
            else{
                MotionToast.createColorToast(this,"Uwaga","Ustaw godzine",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            }
        }
        else{
            ins.text="USTAW PARAMETRY"
        }


    }
}