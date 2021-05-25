package com.example.drinkdrive.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mygallery.Adapter.com.example.drinkdrive.adapters.Alcohol

@Database(entities = [(AlcoholDrunk::class),(Alcohol::class)], version = 3)
abstract class AppDatabase :RoomDatabase() {
    abstract fun alcoholDrunkDAO() : AlcoholDrunkDAO
    abstract fun alcoholDAO() : AlcoholDAO
}