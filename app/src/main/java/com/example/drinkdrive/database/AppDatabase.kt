package com.example.drinkdrive.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(AlcoholDrunk::class)], version = 2)
abstract class AppDatabase :RoomDatabase() {
    abstract fun alcoholDrunkDAO() : AlcoholDrunkDAO
}