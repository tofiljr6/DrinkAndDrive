package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

@Dao
interface AlcoholDAO{

    @Query("select * from alcohols where userId=:id or userId is null")
    fun getAll(id:String) : MutableList<Alcohol>

    @Insert
    fun insertAll(vararg alcohol: Alcohol)

    @Query("select name from alcohols limit 1")
    fun getFirstName():String

    @Query("delete from alcohols")
    fun deleteAll()

    @Delete
    fun delete(alcohol: Alcohol)

    @Query("insert into alcohols(`name`,`photo`,`capacity`,`percent`,`userId`) values(:name,:photo,:capacity,:percent,:userId)")
    fun insert(name:String,photo:String,capacity:Float,percent:Float,userId:String)

    @Query("update alcohols set percent=:percent, capacity=:capacity where id=:id")
    fun set(id:Int,capacity: Float,percent: Float)

    @Query("select id from alcohols order by id desc limit 1")
    fun getLastID() : Int
}