package com.example.drinkdrive.fragments

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.GraphActivity
import com.example.drinkdrive.adapters.HistoryRecyclerAdapter
import com.example.drinkdrive.adapters.SwipeToDelete
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception


class HistoryFragment : Fragment() {

    lateinit var database: AppDatabase
    private var items= mutableListOf<AlcoholDrunk>()
    private lateinit var recycler:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_history, container, false)
        try {
            database = Room.databaseBuilder(
                view!!.context,
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }
        val user = Firebase.auth.currentUser!!.uid
        items=database.alcoholDrunkDAO().getAll(user.toString())
        val adapter= HistoryRecyclerAdapter(items)
        recycler=view!!.findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager =
            LinearLayoutManager(view!!.context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter=adapter
        val swiper=object: SwipeToDelete(view!!.context,0, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val removed=items[position]
                GlobalScope.launch {
                    database.alcoholDrunkDAO().delete(removed)
                }
                items.removeAt(position)
                adapter.notifyItemRemoved(position)
                if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE && recycler.layoutParams.width==1000){
                    val frag=fragmentManager!!.findFragmentById(R.id.chartFrag) as ChartFragment
                    frag.display(items as ArrayList<AlcoholDrunk>)
                }
                Snackbar.make(viewHolder.itemView, removed.alcohol_name+" at "+removed.data+" removed", Snackbar.LENGTH_LONG).setBackgroundTint(
                    Color.BLACK)
                    .setTextColor(Color.WHITE)
                    .setAction("UNDO") {
                        items.add(position,removed)
                        adapter.notifyItemInserted(position)
                        if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE && recycler.layoutParams.width==1000){
                            val frag=fragmentManager!!.findFragmentById(R.id.chartFrag) as ChartFragment
                            frag.display(items as ArrayList<AlcoholDrunk>)
                        }
                        GlobalScope.launch {
                            database.alcoholDrunkDAO().insertAll(removed)
                        }
                    }.show()
            }
        }
        val itemTouchHelper= ItemTouchHelper(swiper)
        itemTouchHelper.attachToRecyclerView(recycler)
        val button=view!!.findViewById<ToggleButton>(R.id.toggleButton)
        val action=view!!.findViewById<Button>(R.id.button6).setOnClickListener{
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val myIntent= Intent(view!!.context, GraphActivity::class.java)
                myIntent.putExtra("data",ArrayList<AlcoholDrunk>(items))
                startActivity(myIntent)
            } else {
                val frag=fragmentManager!!.findFragmentById(R.id.chartFrag) as ChartFragment
                frag.display(items as ArrayList<AlcoholDrunk>)
                recycler.layoutManager =
                    LinearLayoutManager(view!!.context, LinearLayoutManager.VERTICAL, false)
                recycler.layoutParams.width=1000
            }

            true
        }
        val spinner=view!!.findViewById<Spinner>(R.id.spinner)
        val arrayAdapter= ArrayAdapter<String>(view!!.context,android.R.layout.simple_list_item_1,resources.getStringArray(R.array.sortStyle))
        spinner.adapter=arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(!button.isChecked) {
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
            if(!button.isChecked) {
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
        return view
    }

    fun show(){
        recycler.layoutManager =
            LinearLayoutManager(view!!.context, LinearLayoutManager.VERTICAL, false)
        recycler.layoutParams.width=1600
    }


}