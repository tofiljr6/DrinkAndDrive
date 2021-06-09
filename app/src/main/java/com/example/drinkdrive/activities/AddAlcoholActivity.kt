package com.example.drinkdrive.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.room.Room
import com.example.drinkdrive.R
import com.example.drinkdrive.database.AppDatabase
import kotlinx.android.synthetic.main.activity_set_alcohol.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import java.lang.Exception

class AddAlcoholActivity : AppCompatActivity() {

    lateinit var image:ImageView
    lateinit var name:EditText
    lateinit var capacity:EditText
    lateinit var percent:Spinner
    lateinit var database:AppDatabase
    var uri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alcohol)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title="Add alcohol"
         val percentArray= arrayListOf<Int>()
        for(i in 0 .. 100){
            percentArray.add(i)
        }
        percent=findViewById<Spinner>(R.id.alcoholPercent)
        val adapterPercent=
            ArrayAdapter<Int>(this,R.layout.support_simple_spinner_dropdown_item,percentArray)
        percent.adapter=adapterPercent
        name=findViewById(R.id.alcoholName)
        capacity=findViewById(R.id.alcoholCapacity)
        image=findViewById(R.id.imageView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun confirm(view: View) {
        if(name.text.isEmpty()){
            MotionToast.createColorToast(this,"Fill all the details","Name cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this,R.font.helvetica_regular))
            return
        }
        intent.putExtra("name",name.text.toString())
        if(capacity.text.isEmpty()){
            MotionToast.createColorToast(this,"Fill all the details","Capacity cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this,R.font.helvetica_regular))
            return
        }
        intent.putExtra("capacity",capacity.text.toString().toFloat())
        if(uri==null) {
            MotionToast.createColorToast(
                this, "Fill all the details", "Photo cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.helvetica_regular)
            )
            return
        }
        intent.putExtra("uri",uri.toString())
        if(percent.selectedItem.toString()=="0") {
            MotionToast.createColorToast(
                this, "Fill ale the details", "Set percent",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.helvetica_regular)
            )
            return
        }
        intent.putExtra("percent",percent.selectedItem.toString().toFloat())
        setResult(Activity.RESULT_OK,intent)
        finish()

    }

    fun takePhoto(view: View) {
        val myIntent= Intent(this,PhotoActivity::class.java)
        startActivityForResult(myIntent,123)
    }
    fun takeGallery(view: View) {
        val myIntent= Intent(Intent.ACTION_OPEN_DOCUMENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(myIntent,124)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == 123) {
                uri=(data.getStringExtra("data")!!.toUri())
                image.setImageURI(uri)
            }
            if(requestCode==124){
                uri=data.data
                image.setImageURI(uri)
            }
        }
    }
}