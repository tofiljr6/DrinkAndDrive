package com.example.drinkdrive.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart:LineChart
    private val data= mutableListOf<Entry>()
    private val date =  mutableListOf<String>()
    private var wypite =  mutableListOf<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)


        // take a data from intent
        val items = intent.getParcelableArrayListExtra<AlcoholDrunk>("data")

        // only for tests
        val test = findViewById<TextView>(R.id.testViewGraph)
        test.text = items.toString()

        // groupy by date and capacity
        for (item in items!!) {
            if (item.data.substring(0, 11) !in date) {
                date.add(item.data.substring(0, 11))
                wypite.add(item.capacity)
            } else {
                val x = date.lastIndexOf(item.data.substring(0, 11))
                wypite[x] += item.capacity
            }
        }

        // only for test
        test.text = date.toString() + wypite.toString()

        // find the charts and adds values to them
        lineChart = findViewById(R.id.line_chart)
        wypite.reverse()
        var i = 0
        for (w in wypite) {
            data.add(Entry(i.toFloat(), wypite[i]))
            i++
        }

        // axis formatter
        val formatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return date.get(date.size - 1 - value.toInt())
                }
            }
        val xAxis = lineChart.xAxis
        xAxis.setGranularity(1f)
        xAxis.valueFormatter = formatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // draw chart
        val lineDataSet = LineDataSet(data, "Dane")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}