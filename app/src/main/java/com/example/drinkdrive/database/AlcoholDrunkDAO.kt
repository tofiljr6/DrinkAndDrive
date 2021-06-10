package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

@Dao
interface AlcoholDrunkDAO {

    @Query("select * from alcohol_drunk where userId=:id or userId is null")
    fun getAll(id:String) : MutableList<AlcoholDrunk>

    @Insert
    fun insertAll(vararg alcoholDrunk: AlcoholDrunk)

    @Query("insert into alcohol_drunk(`alcohol_name`,`percent_number`,`capacity`,`data_of_consumption`,`userId`) values(:name,:percent,:capacity,:date,:userId)")
    fun insert(name:String,percent:Float,capacity:Float,date:String,userId:String)

    @Query("select id from alcohol_drunk order by id desc limit 1")
    fun getLastID() : Int

    @Delete
    fun delete(alcoholDrunk: AlcoholDrunk)

    @Query("delete from alcohol_drunk where userId=:userId")
    fun deleteAll(userId: String)

    @Query("select sum(capacity) from alcohol_drunk group by substr(data_of_consumption, 0, 11) and userId=:userId")
    fun getSums(userId: String) : MutableList<Float>

    @Query("select data_of_consumption from alcohol_drunk group by substr(data_of_consumption, 0, 11) and userId=:userId")
    fun getDates(userId: String) : MutableList<String>

   @Query("select * from alcohol_drunk where data_of_consumption >= datetime('now', '-24 hours') and userId=:userId order by id desc")
    //@Query("select * from alcohol_drunk where data_of_consumption >= date('now') and userId=:userId order by id desc")
    fun getLastDrunk(userId: String) : MutableList<AlcoholDrunk>

    @Query("select substr(data_of_consumption, 12) from alcohol_drunk where data_of_consumption >= datetime('now', '-24 hours')  and userId=:userId")
    fun getLastDrunkTime(userId: String) : String

    @Query("select data_of_consumption from alcohol_drunk where data_of_consumption >= datetime('now', '-24 hours') and userId=:userId")
    fun getFullLastDrunkTime(userId: String) : String

}