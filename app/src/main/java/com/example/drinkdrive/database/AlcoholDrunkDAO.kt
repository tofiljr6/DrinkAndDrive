package com.example.drinkdrive.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlcoholDrunkDAO {
    @Query("select * from alcohol_drunk")
    fun getAll() : List<AlcoholDrunk>

    @Insert
    fun insertAll(vararg alcoholDrunk: AlcoholDrunk)

    @Query("select id from alcohol_drunk order by id desc limit 1")
    fun getLastID() : Int
}