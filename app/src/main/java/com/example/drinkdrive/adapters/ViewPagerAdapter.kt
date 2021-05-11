package com.example.mygallery.Adapter.com.example.drinkdrive.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drinkdrive.R


class ViewPagerAdapter(private val data:List<Alcohol>):RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val photo:ImageView
        val name:TextView
        val percent:ProgressBar
        val percentNum:TextView
        init{
            photo=view.findViewById(R.id.alcoholImage);
            name=view.findViewById(R.id.alcoholName)
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
       Glide.with(holder.itemView)
           .load(item.photoURL)
           .into(holder.photo)
        holder.name.text=item.name
        holder.percent.setProgress(item.percent.toInt())
        holder.percentNum.text=item.percent.toString()
    }

    override fun getItemCount(): Int {
        return data.size;
    }

}