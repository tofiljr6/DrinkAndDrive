package com.example.mygallery.Adapter.com.example.drinkdrive.adapters

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName="alcohols")
data class Alcohol(
    @ColumnInfo(name="id") @PrimaryKey(autoGenerate = true) var id:Int,
    @ColumnInfo(name="name") var name:String,
    @ColumnInfo(name="photo") var photoURL:String,
    @ColumnInfo(name="capacity")var capacity:Float,
    @ColumnInfo(name="percent") var percent:Float):Parcelable {
}
