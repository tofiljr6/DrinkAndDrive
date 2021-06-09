package com.example.drinkdrive.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SetAlcoholActivity : AppCompatActivity() {
    private lateinit var database : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alcohol)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title="Set alcohol parameters"

        try {
            database = Room.databaseBuilder(
                this,
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }

        val item=intent.getParcelableExtra<Alcohol>("alcohol")
        val textView=findViewById<TextView>(R.id.alcoholName)
        val imageView=findViewById<ImageView>(R.id.alcoholImage)
        val percent= arrayListOf<Int>()
        for(i in 0 .. 100){
            percent.add(i)
        }
        val capacity=findViewById<EditText>(R.id.alcoholCapacity)
        val spinnerPercent=findViewById<Spinner>(R.id.spinnerPercent)
        val adapterPercent=ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,percent)
        spinnerPercent.adapter=adapterPercent
        textView.text=item!!.name
        Glide.with(this)
            .load(item.photoURL)
            .fitCenter()
            .transform(RoundedCorners(100))
            .into(imageView)
        capacity.setText(item.capacity.toString())
        spinnerPercent.setSelection((item.percent.toInt()))
        findViewById<Button>(R.id.confirm).setOnClickListener{
            intent.putExtra("id",item.id)
            intent.putExtra("capacity",capacity.text.toString().toFloat())
            intent.putExtra("percent",spinnerPercent.selectedItem.toString().toFloat())
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        findViewById<Button>(R.id.save).setOnClickListener{
            val id=item.id
            val capacityText=capacity.text.toString().toFloat()
            val percent=spinnerPercent.selectedItem.toString().toFloat()
            GlobalScope.launch {
                var currentDateTime = LocalDateTime.now()
                val user = Firebase.auth.currentUser!!.uid
                database.alcoholDrunkDAO().insert(item.name,percent,capacityText,currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),user.toString())
            }
            MotionToast.createColorToast(this,"Added","You drunk: "+item.name,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this,R.font.helvetica_regular))
            intent.putExtra("id",item.id)
            intent.putExtra("capacity",capacityText)
            intent.putExtra("percent",percent)
            setResult(Activity.RESULT_OK,intent)
            finish()
            true
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}