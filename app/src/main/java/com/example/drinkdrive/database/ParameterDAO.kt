package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ParameterDAO {

    @Query("select * from parameters where userId=:id")
    fun getAll(id:String) : MutableList<Parameters>

    @Query("update parameters set gender=:gender,weight=:weight,height=:height,allowed=:allowed where userId=:userId")
    fun set(gender:String,weight:Float,height:Float,userId:String,allowed: Float)

    @Insert
    fun insertAll(vararg parameters: Parameters)

    @Query("insert into parameters(`gender`,`weight`,`height`,`userId`,`allowed`) values(:gender,:weight,:height,:userId,:allowed)")
    fun insert(gender:String,weight:Float,height:Float,userId:String,allowed:Float)




}