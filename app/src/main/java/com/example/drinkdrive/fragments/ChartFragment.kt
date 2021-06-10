package com.example.drinkdrive.fragments

import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.activity_graph.*


class ChartFragment : Fragment() {

    private lateinit var lineChart : LineChart
    private lateinit var pieChart : PieChart
    private var dataline= mutableListOf<Entry>()
    private var datapie= mutableListOf<PieEntry>()
    private var date =  mutableListOf<String>()
    private var wypite =  mutableListOf<Float>()
    private var typeOfAlco = mutableListOf<String>()
    private var typeOfAlcoPopulariti = mutableListOf<Float>()
    private var selected:String="l"
    private var isModified : Boolean = false
    private lateinit var database: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    fun pieChartDraw() {
        selected="p"
        // make visible right chart
        pieChart.visibility = View.VISIBLE
        lineChart.visibility = View.INVISIBLE

        val pieDataSet = PieDataSet(datapie, "Favourite drinks")
       // pieDataSet.setAutomaticallyDisableSliceSpacing(true)
        pieDataSet.setColors(intArrayOf(R.color.col1, R.color.col2, R.color.col3,
            R.color.col4, R.color.col5, R.color.col6,
            R.color.col7, R.color.col8, R.color.col9, R.color.col10), view!!.context)
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }

    fun lineChartDraw() {
        selected="l"
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
                    if(date.size-1-value.toInt()<0){
                        return ""
                    }
                    return date[date.size - 1 - value.toInt()].substring(5)
                }
            }
        val xAxis = lineChart.xAxis
        xAxis.setGranularity(1f)
        xAxis.valueFormatter = formatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // draw chart
        val lineDataSet = LineDataSet(dataline, "Consumption of alcohol in last days")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.animateX(100)
        lineChart.invalidate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view=inflater.inflate(R.layout.fragment_chart, container, false)
        // find the charts
        pieChart = view!!.findViewById(R.id.pie_chart)

        lineChart = view!!.findViewById(R.id.line_chart)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            view!!.visibility=View.INVISIBLE
            pieChart.setOnLongClickListener{
                view!!.visibility=View.INVISIBLE
                val frag=fragmentManager!!.findFragmentById(R.id.historyFrag) as HistoryFragment
                frag.show()
                true
            }
            lineChart.setOnLongClickListener{
                view!!.visibility=View.INVISIBLE
                val frag=fragmentManager!!.findFragmentById(R.id.historyFrag) as HistoryFragment
                frag.show()
                true
            }
        }
        // take a data from intent
        val items = activity!!.intent.getParcelableArrayListExtra<AlcoholDrunk>("data")

        if(items!=null) {
            calculate(items)
        }

        // spinner
        val spinner = view!!.findViewById<Spinner>(R.id.spinnerGraph)
        val arrayAdpter = ArrayAdapter<String>(view!!.context,
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
        return view
    }

    fun calculate(items:ArrayList<AlcoholDrunk>){
        isModified=false
        date= mutableListOf()
        wypite= mutableListOf()
        typeOfAlcoPopulariti= mutableListOf()
        typeOfAlco= mutableListOf()
        dataline= mutableListOf()
        datapie= mutableListOf()
        // group by date and capacity
        for (item in items) {
                if (item.data.substring(0, 11) !in date) {
                    date.add(item.data.substring(0, 11))
                    wypite.add(item.capacity * item.percent_number / 120)
                } else {
                    val x = date.lastIndexOf(item.data.substring(0, 11))
                    wypite[x] += item.capacity * item.percent_number / 120
                }

                if (item.alcohol_name !in typeOfAlco) {
                    typeOfAlco.add(item.alcohol_name)
                    typeOfAlcoPopulariti.add(1f)
                } else {
                    val x = typeOfAlco.lastIndexOf(item.alcohol_name)
                    typeOfAlcoPopulariti[x] += 1f
                }

            }
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
        pieChart.description.text=""
        lineChart.description.text=""
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
        date.reverse()
        if(selected=="l") {
            lineChartDraw()
        }
        else{
            pieChartDraw()
        }

    }

    fun display(items:ArrayList<AlcoholDrunk>){
        view!!.visibility=View.VISIBLE
        calculate(items)

    }


}