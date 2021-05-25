package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.drinkdrive.R
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
        lineChart=findViewById(R.id.line_chart)
        data.add(Entry(0F, 20F))
        data.add(Entry(1F, 30F))
        data.add(Entry(5F, 40F))
        val lineDataSet=LineDataSet(data,"Dane")
        val lineData=LineData(lineDataSet)
        lineChart.data=lineData
        lineChart.invalidate()
    }
}