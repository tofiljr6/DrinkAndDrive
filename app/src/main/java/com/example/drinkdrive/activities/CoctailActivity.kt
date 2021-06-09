package com.example.drinkdrive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.drinkdrive.R
import com.koushikdutta.ion.ImageViewBitmapInfo
import com.koushikdutta.ion.Ion
import com.skydoves.progressview.ProgressView
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random


class CoctailActivity : AppCompatActivity() {

    private lateinit var imageView:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coctail)
        imageView=findViewById(R.id.coctailImg)
        findViewById<Button>(R.id.button12).setOnClickListener {
            generate()
            true
        }
        generate()

    }

    fun processData(data:String){
        val jsonObject=JSONObject(data)
        val jsonArray=jsonObject.getJSONArray("drinks")
        val json=jsonArray.getJSONObject(0)
        val ingredients= arrayListOf<String>()
        val measures= arrayListOf<String>()
        val ingr=findViewById<TextView>(R.id.ingredients)
        ingr.text=""
        findViewById<TextView>(R.id.name).text=json.getString("strDrink")
        findViewById<TextView>(R.id.type).text=json.getString("strAlcoholic")
        findViewById<TextView>(R.id.glass).text=json.getString("strGlass")
        findViewById<TextView>(R.id.instructions).text=json.getString("strInstructions")
        val url=json.getString("strDrinkThumb")
        var counter=0
        for(i in 1 until 15){
            val info=json.getString("strIngredient$i")
            val amount=json.getString("strMeasure$i")
            if(info!="null") {
                counter++
                ingr.append("$info $amount \n")
            }
            else{
                break
            }
        }
        findViewById<ProgressView>(R.id.progressView).progress=counter.toFloat()*10/15
        findViewById<ProgressView>(R.id.progressView2).progress= (Math.random()*10).toFloat()
        Glide.with(this)
            .load(url)
            .into(imageView)

    }

    fun generate() {
        Ion.with(this)
            .load("https://www.thecocktaildb.com/api/json/v1/1/random.php")
            .asString()
            .setCallback{e,result->
                processData(result)
            }
    }
}