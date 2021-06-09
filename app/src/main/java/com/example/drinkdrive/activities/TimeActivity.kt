package com.example.drinkdrive.activities

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.example.drinkdrive.database.Parameters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

    private var user:String?=null
    private var hour = 0
    private var minute= 0
    private var savedHour = 0
    private var savedMinute= 0

    //woman body water to total weight ratio
    val manRatio = 0.68
    //man body water to total weight ratio
    val womanRatio = 0.55

    // rate at which alcohol is metabolized [permille per hour]
    val womanAlcoholMetabolizeRate = 0.17

    val manAlcoholMetabolizeRate = 0.15

    val alcDensity = 0.78945


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

            result.text = "Please enter parameters before you use this option"
            return
        }else if(selectedHour.text == ""){
            result.text = "Please enter hour"
            return
        }else if(!today.isChecked && !tomorrow.isChecked){
            result.text = "Please select day"
            return
        }
        val parameters = database.parameterDAO().getAll(user!!)[0]

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        var lastDrinkTimeString = database.alcoholDrunkDAO().getFullLastDrunkTime()

        val lastDrinkTime: LocalDateTime
        if(lastDrinkTimeString != null) {
            lastDrinkTime =
                LocalDateTime.parse(database.alcoholDrunkDAO().getFullLastDrunkTime(), formatter)
        }else{
            lastDrinkTime = LocalDateTime.now()
        }

        val timeToEnd = getTimeFromWidget()
        Log.v("XDX", timeToEnd.toString())

        val hours = Duration.between(lastDrinkTime, timeToEnd).toMinutes().toDouble() / 60

        val alcoholMass =howMuchCanIDrink(lastDrinks,parameters, hours)
        //val howMuchCanIDrink = calculatorService.howMuchCanIDrink(0.03f, "MALE",80f, 8f)
        //val alcoholBloodContent = calculatorService.getBloodAlcoholContent(0.03f,"MALE",80f,2f)


        val alcoholAmount = (getAlcohols(alcoholMass))

        if(alcoholMass <= 0.0 ){
            result.text = "You can't drink more, you won't get sober!"
        }else {
            result.text =
                "You can drink $alcoholMass grams of alcohol that is:\n$alcoholAmount"
        }
    }

    private fun getTimeFromWidget(): LocalDateTime{

        var time = LocalDateTime.of(LocalDate.now(), LocalTime.of(savedHour,savedMinute))

        if(tomorrow.isChecked){
            time = time.plusDays(1)
        }
        return time
    }

    //time- amount time during which alcohol was present in the blood [hour]
    //result in permille

    fun getBloodAlcoholContent(lastDrinks: MutableList<AlcoholDrunk>, parameters: Parameters, time: Double): Double{

        val alcoholMass = getAlcoholMass(lastDrinks)
        val gender = parameters.gender
        val weight = parameters.weight
        if(gender.equals("MALE",true)){

            return (((alcoholMass/(manRatio * weight))) - manAlcoholMetabolizeRate * time)
        }else {

            return (((alcoholMass / (womanRatio * weight))) - womanAlcoholMetabolizeRate * time)
        }
    }

    fun getAlcoholMass(lastDrinks :MutableList<AlcoholDrunk>): Double{

        //alcohol density g/ml

        var alcoholMass = 0.0

        if (lastDrinks.size != 0) {
            for (l in lastDrinks) {
                //divide by 100 to get percentage value from percent number

                alcoholMass += l.capacity * alcDensity* l.percent_number  / 100
            }
        }

        return alcoholMass
    }

    fun getAlcohols(alcoholMass : Double): String{

        val winePercentage = 0.2
        val beerPercentage = 0.05
        val vodkaPercentage = 0.4

        var beerAmount =  roundOffDecimal((alcoholMass * alcDensity) / beerPercentage)
        var wineAmount =  roundOffDecimal((alcoholMass * alcDensity) / winePercentage)
        var vodkaAmount = roundOffDecimal((alcoholMass * alcDensity) / vodkaPercentage)



        return "$beerAmount ml of 5% beer\n$wineAmount ml of 20% wine\n$vodkaAmount ml of 40% vodka"
    }

    fun howMuchCanIDrink(lastDrinks: MutableList<AlcoholDrunk>, parameters: Parameters, time: Double): Double {

        val alcoholMass = getAlcoholMass(lastDrinks)
        val gender = parameters.gender
        val weight = parameters.weight


        if(gender.equals("MALE", true)){

            return roundOffDecimal(((manAlcoholMetabolizeRate * time) * manRatio * weight) - alcoholMass)!!
        }else {

            return roundOffDecimal((( womanAlcoholMetabolizeRate * time) * womanRatio * weight) - alcoholMass)!!
        }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
}