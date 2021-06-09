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
import com.firebase.ui.auth.ui.ProgressView
import com.koushikdutta.ion.ImageViewBitmapInfo
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject

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
        //findViewById<>(R.id.progressView).setProgress(1)


        for(i in 1 until 15){
            val info=json.getString("strIngredient$i")
            val amount=json.getString("strMeasure$i")
            if(info!="null") {
                ingr.append("\n $info $amount")

            }
            else{
                break
            }

        }
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