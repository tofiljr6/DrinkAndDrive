package com.example.drinkdrive.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_parameters.*


class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart : LineChart
    private lateinit var pieChart : PieChart
    private var dataline= mutableListOf<Entry>()
    private val datapie= mutableListOf<PieEntry>()
    private var date =  mutableListOf<String>()
    private var wypite =  mutableListOf<Float>()
    private var typeOfAlco = mutableListOf<String>()
    private var typeOfAlcoPopulariti = mutableListOf<Float>()
    private var isModified : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        // take a data from intent
        val items = intent.getParcelableArrayListExtra<AlcoholDrunk>("data")

        // group by date and capacity
        for (item in items!!) {
            if (item.data.substring(0, 11) !in date) {
                date.add(item.data.substring(0, 11))
                wypite.add(item.capacity)
            } else {
                val x = date.lastIndexOf(item.data.substring(0, 11))
                wypite[x] += item.capacity
            }

            if (item.alcohol_name !in typeOfAlco) {
                typeOfAlco.add(item.alcohol_name)
                typeOfAlcoPopulariti.add(1f)
            } else {
                val x = typeOfAlco.lastIndexOf(item.alcohol_name)
                typeOfAlcoPopulariti[x] += 1f
            }
        }

        if (date.size == 1) {
            val newdate = mutableListOf<String>()
            val todayday = date[0].substring(8, 10).toInt()
            val todaymonth = date[0].substring(5, 7)
            newdate.add((todayday - 1).toString() + "-" + todaymonth)
            newdate.add((todayday).toString() + "-" + todaymonth)
            newdate.add((todayday + 1).toString() + "-" + todaymonth)
            date = newdate
            isModified = true
        }

        // find the charts
        pieChart = findViewById(R.id.pie_chart)
        lineChart = findViewById(R.id.line_chart)

        // add values to data (pie and line)
        var i = 0
        for (t in typeOfAlco) {
            datapie.add(PieEntry(typeOfAlcoPopulariti[i], typeOfAlco[i]))
            i++
        }
        i = 0
        for (w in wypite) {
            dataline.add(Entry(i.toFloat(), wypite[i]))
            i++
        }

        // spinner
        val spinner = findViewById<Spinner>(R.id.spinnerGraph)
        val arrayAdpter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.graphType))
        spinner.adapter = arrayAdpter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                when (position) {
                    0 -> lineChartDraw()
                    1 -> pieChartDraw()
                }
            }
        }
    }

    fun pieChartDraw() {
        // make visible right chart
        pieChart.visibility = View.VISIBLE
        lineChart.visibility = View.INVISIBLE

        val pieDataSet = PieDataSet(datapie, "pie chart")
        pieDataSet.setAutomaticallyDisableSliceSpacing(true)
        pieDataSet.setColors(intArrayOf(R.color.col1, R.color.col2, R.color.col3,
                R.color.col4, R.color.col5, R.color.col6,
                R.color.col7, R.color.col8, R.color.col9, R.color.col10), this)
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }

    fun lineChartDraw() {
        // make visible right chart
        pieChart.visibility = View.INVISIBLE
        lineChart.visibility = View.VISIBLE

        // axis formatter
        val formatter: ValueFormatter =
                object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase): String {
                        if (isModified) {
                            return date[(value + 1).toInt()]
                        }
                        return date[date.size - 1 - value.toInt()].substring(5)
                    }
                }
        val xAxis = lineChart.xAxis
        xAxis.setGranularity(1f)
        xAxis.valueFormatter = formatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // draw chart
        val lineDataSet = LineDataSet(dataline, "Spożycie alkoholu w ostatnich dniach")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.animateX(100)
        lineChart.invalidate()
    }
}