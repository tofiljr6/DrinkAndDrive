package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart:LineChart
    private val data= mutableListOf<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        val items=intent.getParcelableArrayListExtra<AlcoholDrunk>("data")
        lineChart=findViewById(R.id.line_chart)
        for(item in items!!){
            data.add(Entry(item.capacity,item.percent_number))
        }
        val lineDataSet=LineDataSet(data,"Dane")
        val lineData=LineData(lineDataSet)
        lineChart.data=lineData
        lineChart.invalidate()
    }
}