package com.example.drinkdrive.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

@Database(entities = [(AlcoholDrunk::class),(Alcohol::class),(Parameters::class)], version = 6)
abstract class AppDatabase :RoomDatabase() {
    abstract fun alcoholDrunkDAO() : AlcoholDrunkDAO
    abstract fun alcoholDAO() : AlcoholDAO
    abstract fun parameterDAO() : ParameterDAO
}