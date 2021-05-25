package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

@Dao
interface AlcoholDAO{

    @Query("select * from alcohols")
    fun getAll() : MutableList<Alcohol>

    @Insert
    fun insertAll(vararg alcohol: Alcohol)

    @Query("select name from alcohols limit 1")
    fun getFirstName():String

    @Query("delete from alcohols")
    fun deleteAll()

    @Delete
    fun delete(alcohol: Alcohol)

    @Query("insert into alcohols(`name`,`photo`,`capacity`,`percent`) values(:name,:photo,:capacity,:percent)")
    fun insert(name:String,photo:String,capacity:Float,percent:Float)

    @Query("update alcohols set percent=:percent, capacity=:capacity where id=:id")
    fun set(id:Int,capacity: Float,percent: Float)
}