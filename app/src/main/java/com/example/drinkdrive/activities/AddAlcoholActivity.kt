package com.example.drinkdrive.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Gallery
import android.widget.ImageView
import androidx.core.net.toUri
import com.example.drinkdrive.R

class AddAlcoholActivity : AppCompatActivity() {

    lateinit var image:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alcohol)
        image=findViewById(R.id.imageView)
    }

    fun confirm(view: View) {
        finish()
    }

    fun takePhoto(view: View) {
        val myIntent= Intent(this,PhotoActivity::class.java)
        startActivityForResult(myIntent,123)
    }
    fun takeGallery(view: View) {
        val myIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(myIntent,124)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null) {
            if (requestCode == 123) {
                image.setImageURI(data.getStringExtra("data")!!.toUri())
            }
            if(requestCode==124){
                image.setImageURI(data.data)
            }
        }
    }
}