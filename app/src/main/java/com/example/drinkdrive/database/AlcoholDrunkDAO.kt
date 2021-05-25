package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlcoholDrunkDAO {
    @Query("select * from alcohol_drunk")
    fun getAll() : MutableList<AlcoholDrunk>

    @Insert
    fun insertAll(vararg alcoholDrunk: AlcoholDrunk)

    @Query("insert into alcohol_drunk(`alcohol_name`,`percent_number`,`capacity`,`data_of_consumption`) values(:name,:percent,:capacity,:date)")
    fun insert(name:String,percent:Float,capacity:Float,date:String)

    @Query("select id from alcohol_drunk order by id desc limit 1")
    fun getLastID() : Int

    @Delete
    fun delete(alcoholDrunk: AlcoholDrunk)

    @Query("delete from alcohol_drunk")
    fun deleteAll()


}