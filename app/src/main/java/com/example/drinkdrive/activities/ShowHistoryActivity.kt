package com.example.drinkdrive.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.adapters.HistoryRecyclerAdapter
import com.example.drinkdrive.adapters.SwipeToDelete
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class ShowHistoryActivity : AppCompatActivity() {

    lateinit var database:AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_history)
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

        var items=database.alcoholDrunkDAO().getAll()

        val adapter=HistoryRecyclerAdapter(items)
        val recycler=findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler.adapter=adapter
        val swiper=object: SwipeToDelete(this,0, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val removed=items[position]
                GlobalScope.launch {
                    database.alcoholDrunkDAO().delete(removed)
                }
                items.removeAt(position)
                adapter.notifyItemRemoved(position)
                Snackbar.make(viewHolder.itemView, removed.alcohol_name+" at "+removed.data+" removed", Snackbar.LENGTH_LONG).setBackgroundTint(
                    Color.BLACK)
                    .setTextColor(Color.WHITE)
                    .setAction("UNDO") {
                        items.add(position,removed)
                        adapter.notifyItemInserted(position)
                        GlobalScope.launch {
                            database.alcoholDrunkDAO().insertAll(removed)
                        }
                    }.show()
            }
        }
        val itemTouchHelper= ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(recycler)
        val button=findViewById<ToggleButton>(R.id.toggleButton)
        val spinner=findViewById<Spinner>(R.id.spinner)
        val arrayAdapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,resources.getStringArray(R.array.sortStyle))
        spinner.adapter=arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(button.isChecked) {
                    when (position) {
                        0 -> items.sortBy {  it.data }
                        1 -> items.sortBy { it.alcohol_name }
                        2 -> items.sortBy { it.capacity }
                        3 -> items.sortBy {
                            it.percent_number
                        }
                    }
                }
                else{
                    when (position) {
                        0 -> items.sortByDescending {  it.data }
                        1 -> items.sortByDescending { it.alcohol_name }
                        2 -> items.sortByDescending { it.capacity }
                        3 -> items.sortByDescending {
                            it.percent_number
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
        button.setOnClickListener{
            if(button.isChecked) {
                when (spinner.selectedItemPosition) {
                    0 -> items.sortBy {  it.data }
                    1 -> items.sortBy { it.alcohol_name }
                    2 -> items.sortBy { it.capacity }
                    3 -> items.sortBy {
                        it.percent_number
                    }
                }
            }
            else{
                when (spinner.selectedItemPosition) {
                    0 -> items.sortByDescending {  it.data }
                    1 -> items.sortByDescending { it.alcohol_name }
                    2 -> items.sortByDescending { it.capacity }
                    3 -> items.sortByDescending {
                        it.percent_number
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
            true
        }
    }
