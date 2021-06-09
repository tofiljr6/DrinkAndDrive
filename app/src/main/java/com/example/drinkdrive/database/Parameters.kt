package com.example.drinkdrive.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="parameters")
data class Parameters(
    @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id:Int,
    @ColumnInfo(name="gender") var gender:String,
    @ColumnInfo(name="weight") var weight:Float,
    @ColumnInfo(name="height")var height:Float,
    @ColumnInfo(name="allowed") var allowed:Float,
    @ColumnInfo(name="userId") var userId:String
)
