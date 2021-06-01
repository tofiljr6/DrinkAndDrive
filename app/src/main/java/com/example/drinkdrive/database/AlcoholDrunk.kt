package com.example.drinkdrive.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Entity(tableName = "alcohol_drunk")
@Parcelize
data class AlcoholDrunk (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id : Int,
    @ColumnInfo(name = "alcohol_name") var alcohol_name : String,
    @ColumnInfo(name = "percent_number") var percent_number : Float,
    @ColumnInfo(name = "capacity") var capacity : Float,
    @ColumnInfo(name = "data_of_consumption") var data : String,
    @ColumnInfo(name="userId") var userId:String?
):Parcelable