package com.example.drinkdrive.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.drinkdrive.R
import com.example.drinkdrive.adapters.NotificationAdapter
import com.example.drinkdrive.adapters.ViewPagerClick
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.ViewPagerAdapter
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : AppCompatActivity(),ViewPagerClick {

    private var items= mutableListOf<Alcohol>()
    private lateinit var adapter:ViewPagerAdapter
    private val capacity= arrayListOf<Float>(500F,200F,30F,50F,20F,100F,40F,40F,30F,30F)
    private val percent= arrayListOf<Float>(5F,12.5F,40F,35F,80F,40F,35F,36F,37.5F,40F)
    private lateinit var database:AppDatabase
    private val RC_SIGN_IN=125
    private lateinit var shared: SharedPreferences
    var userId:String?=null
    private var carsIMG = arrayOf(R.drawable.cargreen, R.drawable.caryellow, R.drawable.car2, R.drawable.carblack)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        shared=getSharedPreferences("prefs", Context.MODE_PRIVATE)
        userId=shared.getString("user","noLogged")
        if(userId=="noLogged"){
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)
        }
        else{
            title="User: ${Firebase.auth.currentUser!!.displayName}"
            items = database.alcoholDAO().getAll(userId!!)
            adapter= ViewPagerAdapter(items,database,this, this)
            val viewPager=findViewById<ViewPager2>(R.id.viewPager)
            val tabLayout=findViewById<TabLayout>(R.id.tab)
            viewPager.adapter=adapter
            TabLayoutMediator(tabLayout,viewPager){tab,position->
                tab.text=items[position].name
            }.attach()
        }
        var names = resources.getStringArray(R.array.alcohols)
        var images = resources.getStringArray(R.array.images)
        val firstName = database.alcoholDAO().getFirstName()
        if (firstName != "PIWO") {
            for (i in 0 until names.size) {
                val alcohol = Alcohol(i + 1, names[i], images[i], capacity[i], percent[i], null)
                database.alcoholDAO().insertAll(alcohol)
            }
        }
        createNotificationChannel()
        promile()
    }

    override fun onResume() {
        super.onResume()
        promile()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item1 ->setParameters()
            R.id.item2 ->setTime()
            R.id.item3 ->showHistory()
            R.id.item4 ->openSettings()
            R.id.item5->logOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logOut() {
        val editor=shared.edit()
        editor.putString("user","noLogged")
        editor.commit()
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    private fun setParameters() {
        val myIntent= Intent(this,ParametersActivity::class.java)
        startActivity(myIntent)
    }
    private fun setTime() {
        val myIntent= Intent(this,TimeActivity::class.java)
        startActivity(myIntent)
    }

    private fun showHistory() {
        val myIntent = Intent(this, ShowHistoryActivity::class.java)
        startActivity(myIntent)
    }
    private fun openSettings() {
        val myIntent= Intent(this,SettingsActivity::class.java)
        startActivity(myIntent)
    }

    fun addAlcohol(view: View) {
        val myIntent = Intent(this, AddAlcoholActivity::class.java)
        startActivityForResult(myIntent,123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 123) {
                val name = data.getStringExtra("name")
                val uri = data.getStringExtra("uri")
                val capacity = data.getFloatExtra("capacity", 0F)
                val percent = data.getFloatExtra("percent", 0F)
                database.alcoholDAO().insert(name!!, uri!!, capacity!!, percent!!,userId!!)
                val id=database.alcoholDAO().getLastID()
                items.add(Alcohol(id, name!!, uri!!, capacity, percent,userId))
                adapter.notifyItemInserted(items.size-1)
            }
            if (requestCode == 124) {
                val id = data.getIntExtra("id", 0)
                val capacity = data.getFloatExtra("capacity", 0F)
                val percent = data.getFloatExtra("percent", 0F)
                database.alcoholDAO().set(id, capacity, percent)
                for (item in items) {
                    if (item.id == id) {
                        item.capacity = capacity
                        item.percent = percent
                        adapter.notifyItemChanged(items.indexOf(item))
                        break
                    }
                }
            }
        }
        if(requestCode==RC_SIGN_IN){
            if(data!=null){
                title="User: ${Firebase.auth.currentUser!!.displayName}"
                userId = Firebase.auth.currentUser!!.uid
                val editor=shared.edit()
                editor.putString("user",userId.toString())
                editor.commit()
                items = database.alcoholDAO().getAll(userId!!)
                adapter= ViewPagerAdapter(items,database,this, this)
                val viewPager=findViewById<ViewPager2>(R.id.viewPager)
                val tabLayout=findViewById<TabLayout>(R.id.tab)
                viewPager.adapter=adapter
                TabLayoutMediator(tabLayout,viewPager){tab,position->
                    tab.text=items[position].name
                }.attach()
            }
            else{
                finish()
            }
        }
    }


    override fun onLongClick(position: Int) {
        super.onLongClick(position)
        val myIntent=Intent(this,SetAlcoholActivity::class.java)
        myIntent.putExtra("alcohol",items[position])
        startActivityForResult(myIntent,124)
        true
    }

    private fun createNotificationChannel() {
        val name = "name"
        val descriptionText = "des"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("notification", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(time : Int, active : String) {
        val intent = Intent(this, NotificationAdapter::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (active == "true") {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * time, pendingIntent)
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun promile () {
        // https://pl.wikipedia.org/wiki/Zawarto%C5%9B%C4%87_alkoholu_we_krwi
        var k = 0f // współczyniki płci
        var burnalco = 0

        // get body params data
        val params = database.parameterDAO().getAll(userId!!)[0]
        val sex = params.gender
        val w = params.weight
        if (sex == "male") {
            k = 0.7f
            burnalco = 12
        } else {
            k = 0.6f
            burnalco = 10
        }

        val carTextView = findViewById<TextView>(R.id.promilleTextView)
        val v = findViewById<TextView>(R.id.promilleTextView2)
        val currentcarimg = findViewById<ImageView>(R.id.imageView)

        val last = database.alcoholDrunkDAO().getLastDrunk()
        if (last.size != 0) {
            // czas od którego będziemy liczyć promile
            var startdata = LocalDate.parse(last[0].data.substring(0, 10))
            var starttime = LocalTime.parse(last[0].data.substring(11, 19))
            var ldatacurrent = LocalDate.parse(last[0].data.substring(0, 10))
            var ltimecurrent = LocalTime.parse(last[0].data.substring(11, 19))
            val hourstoStay = LocalTime.parse("01:00:00")

            var doses = 0

            for (l in last) {
                ldatacurrent = LocalDate.parse(l.data.substring(0, 10))
                ltimecurrent = LocalTime.parse(l.data.substring(11, 19))
                when (l.alcohol_name) {
                    "PIWO" -> {
                        if (starttime.plusHours(hourstoStay.hour.toLong())
                                .isBefore(ltimecurrent) || startdata != ldatacurrent
                        ) {
                            starttime = ltimecurrent
                            startdata = ldatacurrent
                            doses = (l.capacity / 250).toInt()
                        } else {
                            val dose = (l.capacity / 250).toInt()
                            doses += dose
                        }
                    }
                    "WINO" -> {
                        if (starttime.plusHours(hourstoStay.hour.toLong())
                                .isBefore(ltimecurrent) || startdata != ldatacurrent
                        ) {
                            starttime = ltimecurrent
                            startdata = ldatacurrent
                            doses = (l.capacity / 100).toInt()
                        } else {
                            val dose = (l.capacity / 100).toInt()
                            doses += dose
                        }
                    }
                    "WÓDKA" -> {
                        if (starttime.plusHours(hourstoStay.hour.toLong())
                                .isBefore(ltimecurrent) || startdata != ldatacurrent
                        ) {
                            starttime = ltimecurrent
                            startdata = ldatacurrent
                            doses = (l.capacity / 30).toInt()
                        } else {
                            val dose = (l.capacity / 30).toInt()
                            doses += dose
                        }
                    }
                    else -> {
                        if (starttime.plusHours(hourstoStay.hour.toLong())
                                .isBefore(ltimecurrent) || startdata != ldatacurrent
                        ) {
                            starttime = ltimecurrent
                            startdata = ldatacurrent
                            doses = (l.capacity / 50).toInt()
                        } else {
                            val dose = (l.capacity / 50).toInt()
                            doses += dose
                        }
                    }
                }
            }

            // kieliszek
            val deltapicia = LocalTime.parse(LocalTime.now().toString()).minusHours(starttime.hour.toLong())
            val mgofBurnAlco = deltapicia.hour * burnalco
            val mgofConcuptedAlco = doses * 10

            val p = (mgofConcuptedAlco - mgofBurnAlco) / (k * w)
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_DOWN
            v.text = df.format(p).toString()

            // autko
            var readytogo = LocalTime.parse("00:00:00")
            readytogo = readytogo.plusHours(doses.toLong())
            val final = starttime.plusHours(readytogo.hour.toLong())

            carTextView.text = final.toString()

            // autko - kolor
            when {
                readytogo.hour <= 0 -> {
                    currentcarimg.setImageResource(carsIMG[0])
                }
                readytogo.hour < 4 -> {
                    currentcarimg.setImageResource(carsIMG[1])
                }
                readytogo.hour < 9 -> {
                    currentcarimg.setImageResource(carsIMG[2])
                }
                else -> {
                    currentcarimg.setImageResource(carsIMG[3])
                }
            }

            // ustawianie powiadomienie
            val finalHour = final.hour * 60 * 60
            val finalMinute = final.minute * 60

            val nowHour = LocalTime.now().hour * 60 * 60
            val nowMinute = LocalTime.now().minute * 60


            createNotification((finalMinute + finalHour + final.second) -
                    (nowHour + nowMinute + LocalTime.now().second),
                shared.getString("notifications", "false")!!)


        } else {
            currentcarimg.setImageResource(carsIMG[0])
            carTextView.text = "GO"
            v.text = "0.0"
        }
    }
}