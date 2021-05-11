package com.example.mygallery.Adapter.com.example.drinkdrive.adapters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Alcohol(var id:Int,var name:String,var photoURL:String,var capacity:Int,var percent:Float):Parcelable {
}
