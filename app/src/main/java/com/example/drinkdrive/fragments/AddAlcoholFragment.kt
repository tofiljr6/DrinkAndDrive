package com.example.drinkdrive.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.MainActivity
import com.example.drinkdrive.activities.PhotoActivity
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.drinkdrive.database.AppDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import www.sanju.motiontoast.MotionToast

class AddAlcoholFragment : Fragment() {

    lateinit var image: ImageView
    lateinit var name: EditText
    lateinit var capacity: EditText
    lateinit var percent:Spinner
    lateinit var database: AppDatabase
    var uri: Uri?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            database = Room.databaseBuilder(
                requireActivity(),
                AppDatabase::class.java,
                "alcoholDrunk.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        } catch (e: Exception) {
            Log.d("db_D&D", e.message.toString())
        }

        val view = inflater.inflate(R.layout.fragment_add_alcohol, container, false)


        val percentArray= arrayListOf<Int>()
        for(i in 0 .. 100){
            percentArray.add(i)
        }
        percent= view.findViewById<Spinner>(R.id.alcoholPercent)
        val adapterPercent=
            ArrayAdapter<Int>(requireActivity(),R.layout.support_simple_spinner_dropdown_item,percentArray)
        percent.adapter=adapterPercent
        name=view.findViewById(R.id.alcoholName)
        capacity=view.findViewById(R.id.alcoholCapacity)
        image=view.findViewById(R.id.imageView)

        view.findViewById<Button>(R.id.button5).setOnClickListener {
            val myIntent= Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(myIntent,124)
        }

        view.findViewById<Button>(R.id.button4).setOnClickListener {
            val myIntent= Intent(requireActivity(), PhotoActivity::class.java)
            startActivityForResult(myIntent,123)
        }

        view.findViewById<Button>(R.id.confirmButton).setOnClickListener {
            confirm()
        }

        return view
    }

    fun confirm() {
        if(name.text.isEmpty()){
            MotionToast.createColorToast(requireActivity(),"Fill all the details","Name cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))
            return
        }
        activity?.intent?.putExtra("name",name.text.toString())
        if(capacity.text.isEmpty()){
            MotionToast.createColorToast(requireActivity(),"Fill all the details","Capacity cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular))
            return
        }
        activity?.intent?.putExtra("capacity",capacity.text.toString().toFloat())
        if(uri==null) {
            MotionToast.createColorToast(
                requireActivity(), "Fill all the details", "Photo cannot be empty",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            )
            return
        }
        activity?.intent?.putExtra("uri",uri.toString())
        if(percent.selectedItem.toString()=="0") {
            MotionToast.createColorToast(
                requireActivity(), "Fill ale the details", "Set percent",
                MotionToast.TOAST_WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            )
            return
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity?.intent?.putExtra("percent",percent.selectedItem.toString().toFloat())
            activity?.setResult(Activity.RESULT_OK,activity?.intent)
            activity?.finish()
        } else {
            val name = name.text.toString()
            val uri = uri.toString()
            val capacity = capacity.text.toString().toFloat()
            val percent = percent.selectedItem.toString().toFloat()
            val shared = activity?.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            val userId = shared?.getString("user","noLogged")!!;
            val items = database.alcoholDAO().getAll(userId)
            database.alcoholDAO().insert(name, uri, capacity, percent, userId)
            val id = database.alcoholDAO().getLastID()
            items.add(Alcohol(id, name, uri, capacity, percent, userId))
            val frag=fragmentManager!!.findFragmentById(R.id.fragment) as MainFragment
            activity?.findViewById<ViewPager2>(R.id.viewPager)?.adapter?.notifyItemInserted(items.size - 1)

            this.name.setText("")
            this.image.setImageResource(0)
            this.capacity.setText("")
            this.percent.setSelection(0)

            MotionToast.createColorToast(
                requireActivity(), "Success", "Alcohol added",
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
            )
        }
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