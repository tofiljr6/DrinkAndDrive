package com.example.mygallery.Adapter.com.example.drinkdrive.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.MainActivity
import com.example.drinkdrive.adapters.ViewPagerClick
import com.example.drinkdrive.database.AppDatabase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ViewPagerAdapter(private val data:List<Alcohol>,private val database: AppDatabase,private val click:ViewPagerClick, private val mainActivity: MainActivity):RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val photo:ImageView
        val percent:ProgressBar
        val percentNum:TextView
        val capacity:TextView
        init{
            photo=view.findViewById(R.id.alcoholImage);
            percent=view.findViewById(R.id.alcoholPercent)
            percentNum=view.findViewById(R.id.alcoholPercentNum)
            capacity=view.findViewById(R.id.capacity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate((R.layout.alcohol_item),parent,false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item=data[position]
        holder.itemView.setOnClickListener{
            GlobalScope.launch {
                var currentDateTime = LocalDateTime.now()
                val user = Firebase.auth.currentUser!!.uid
                database.alcoholDrunkDAO().insert(item.name,item.percent,item.capacity.toFloat(),currentDateTime.format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),user.toString())
            }
            MotionToast.createColorToast(holder.itemView.context as Activity,"Added","You drunk: "+item.name,
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(holder.itemView.context,R.font.helvetica_regular))
            mainActivity.promile() // refresh car time and per mile in the glass
            true
        }
        holder.itemView.setOnLongClickListener {
            click.onLongClick(position)
            true
        }
       Glide.with(holder.itemView)
           .load(item.photoURL)
           .override(400,400)
           .fitCenter()
           .transform(RoundedCorners(100))
           .into(holder.photo)
        holder.percent.progress = item.percent.toInt()
        holder.percentNum.text=item.percent.toString()+" %"
        holder.capacity.text=item.capacity.toInt().toString()+" ml"
    }

    override fun getItemCount(): Int {
        return data.size;
    }

}