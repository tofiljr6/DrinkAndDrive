package com.example.mygallery.Adapter.com.example.drinkdrive.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.SetAlcoholActivity
import com.example.drinkdrive.database.AppDatabase


class ViewPagerAdapter(private val data:List<Alcohol>):RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val photo:ImageView
        val percent:ProgressBar
        val percentNum:TextView
        init{
            photo=view.findViewById(R.id.alcoholImage);
            percent=view.findViewById(R.id.alcoholPercent)
            percentNum=view.findViewById(R.id.alcoholPercentNum)
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
            //SZYBKI SZOT
            true
        }
        holder.itemView.setOnLongClickListener {
            val myIntent=Intent(holder.itemView.context,SetAlcoholActivity::class.java)
            myIntent.putExtra("alcohol",item)
            holder.itemView.context.startActivity(myIntent)
            true
        }
       Glide.with(holder.itemView)
           .load(item.photoURL)
           .into(holder.photo)
        holder.percent.progress = item.percent.toInt()
        holder.percentNum.text=item.percent.toString()
    }

    override fun getItemCount(): Int {
        return data.size;
    }

}