package com.example.drinkdrive.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.drinkdrive.R
import com.example.drinkdrive.adapters.ViewPagerClick
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_add_alcohol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Double.valueOf
import java.lang.Exception

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
                GlobalScope.launch {
                    database.alcoholDAO().insert(name!!, uri!!, capacity!!, percent!!)
                }
                items.add(Alcohol(items.size, name!!, uri!!, capacity, percent))
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

}