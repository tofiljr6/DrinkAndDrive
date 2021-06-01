package com.example.drinkdrive.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.drinkdrive.R
import com.example.drinkdrive.adapters.ViewPagerClick
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_add_alcohol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.lang.Double.valueOf
import java.lang.Exception
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
        var names=resources.getStringArray(R.array.alcohols)
        var images=resources.getStringArray(R.array.images)
        val firstName=database.alcoholDAO().getFirstName()
        if(firstName!="PIWO"){
            for(i in 0 until names.size) {
                val alcohol = Alcohol(i + 1, names[i], images[i], capacity[i], percent[i])
                database.alcoholDAO().insertAll(alcohol)
            }
        }
        items = database.alcoholDAO().getAll()
        adapter= ViewPagerAdapter(items,database,this)
        val viewPager=findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout=findViewById<TabLayout>(R.id.tab)
        viewPager.adapter=adapter
        TabLayoutMediator(tabLayout,viewPager){tab,position->
            tab.text=items[position].name
        }.attach()

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
        }
        return super.onOptionsItemSelected(item)
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
                database.alcoholDAO().insert(name!!, uri!!, capacity!!, percent!!)
                val id=database.alcoholDAO().getLastID()
                items.add(Alcohol(id, name!!, uri!!, capacity, percent))
                adapter.notifyItemInserted(items.size - 1)
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
    }


    override fun onLongClick(position: Int) {
        super.onLongClick(position)
        val myIntent=Intent(this,SetAlcoholActivity::class.java)
        myIntent.putExtra("alcohol",items[position])
        startActivityForResult(myIntent,124)
        true
    }

    fun promile () {
        // https://pl.wikipedia.org/wiki/Zawarto%C5%9B%C4%87_alkoholu_we_krwi
        var k = 0f // współczyniki płci
        var burnalco = 0

        // get body params data
        val shared = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val w = shared.getInt("weight", Context.MODE_PRIVATE).toFloat()
        val sex = shared.getString("gender", "")
        if (sex == "male") {
            k = 0.7f
            burnalco = 12
        } else {
            k = 0.6f
            burnalco = 10
        }

        val carTextView = findViewById<TextView>(R.id.promilleTextView)
        val v = findViewById<TextView>(R.id.promilleTextView2)

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
            val deltapicia = LocalTime.parse(LocalTime.now().toString()).minusHours(starttime.toSecondOfDay().toLong())
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
        }
    }
}