package com.example.drinkdrive.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.example.drinkdrive.R
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private var items= arrayListOf<Alcohol>()
    private lateinit var adapter:ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var names=resources.getStringArray(R.array.alcohols)
        var images=resources.getStringArray(R.array.images)
        for(i in 0 until names.size){
            val alcohol=Alcohol(i+1,names[i],images[i],10F)
            items.add(alcohol)
        }
        adapter= ViewPagerAdapter(items)
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
            R.id.item3 ->addAlcohol()
            R.id.item4 ->showHistory()
            R.id.item5 ->openSettings()
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

    private fun addAlcohol() {
        val myIntent = Intent(this, AddAlcoholActivity::class.java)
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
}