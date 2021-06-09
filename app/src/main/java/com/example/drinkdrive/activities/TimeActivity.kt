package com.example.drinkdrive.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.example.drinkdrive.database.Parameters
import com.example.drinkdrive.service.CalculatorService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.lang.Exception
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class TimeActivity: AppCompatActivity(),TimePickerDialog.OnTimeSetListener {

    private lateinit var selectTime: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var selectedHour: TextView
    private lateinit var calculate: Button
    private lateinit var database:AppDatabase
    private lateinit var today:RadioButton
    private lateinit var tomorrow:RadioButton
    private lateinit var result: TextView
    private var calculatorService: CalculatorService = CalculatorService()
    private var user:String?=null
    private var hour = 0
    private var minute= 0
    private var savedHour = 0
    private var savedMinute= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)


        radioGroup = findViewById(R.id.RadioGroup_activityTime)
        selectedHour = findViewById(R.id.selectedHour_activityDrive)
        selectTime = findViewById(R.id.selectHour_activityTime)
        calculate = findViewById(R.id.calculateButton_activityTime)
        today = findViewById(R.id.today_activityTime)
        tomorrow = findViewById(R.id.tomorrow_activityTime)
        result = findViewById(R.id.resultTextView_activityTime)
        calculate.setOnClickListener {
            calculate()
        }
        user= Firebase.auth.currentUser!!.uid
        selectTime.setOnClickListener {
            getTime()

            TimePickerDialog(this, this,hour, minute,true).show()
        }

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


    private fun getTime(){
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }


    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        var time = ""
        if(savedMinute < 10) {
            time = "$savedHour : 0$savedMinute"
        }
        else {
            time = "$savedHour : $savedMinute"
        }


        selectedHour.text = time
    }



    private fun calculate(){

        val lastDrinks = database.alcoholDrunkDAO().getLastDrunk()
        val all =  database.parameterDAO().getAll(user!!)
        if(all.size == 0) {

            result.text = "Wprowadz parametry zanim skorzystasz z tej opcji!"
        }
        val parameters = database.parameterDAO().getAll(user!!)[0]

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val lastDrinkTime = LocalDateTime.parse(database.alcoholDrunkDAO().getFullLastDrunkTime(),formatter)


        val timeToEnd = getTimeFromWidget()


        val hours = Duration.between(lastDrinkTime, timeToEnd).toMinutes().toDouble() / 60



        val alcoholMass = calculatorService.howMuchCanIDrink(lastDrinks,parameters, hours)
        //val howMuchCanIDrink = calculatorService.howMuchCanIDrink(0.03f, "MALE",80f, 8f)
        //val alcoholBloodContent = calculatorService.getBloodAlcoholContent(0.03f,"MALE",80f,2f)


        val alcoholAmount = (calculatorService.getAlcohols(alcoholMass))

        if(alcoholMass < 0.0 ){
            result.text = "Nie możesz już pic nie zdążysz wytrzeżwieć!"
        }else {
            result.text =
                "Możesz wypic jeszcze $alcoholMass gramow alkoholu to jest:\n$alcoholAmount"
        }
    }




    private fun getTimeFromWidget(): LocalDateTime{

        var time = LocalDateTime.of(LocalDate.now(), LocalTime.of(savedHour,savedMinute))

        if(tomorrow.isChecked){
            time = time.plusDays(1)
        }
        return time
    }
}