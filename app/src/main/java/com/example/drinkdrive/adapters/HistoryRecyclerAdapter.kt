package com.example.drinkdrive.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drinkdrive.R
import com.example.drinkdrive.activities.SetAlcoholActivity
import com.example.drinkdrive.database.AlcoholDrunk
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol


class HistoryRecyclerAdapter(private val data:List<AlcoholDrunk>):RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView
        val capacity: TextView
        val date:TextView
        init{
            name=view.findViewById(R.id.alcoName);
            capacity=view.findViewById(R.id.alcoCapacity)
            date=view.findViewById(R.id.alcoDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate((R.layout.history_item), parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return data.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=data[position]
        holder.name.text=item.alcohol_name
        holder.capacity.text=item.capacity.toString()
        holder.date.text=item.data
    }

}
