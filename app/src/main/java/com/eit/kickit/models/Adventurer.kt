package com.eit.kickit.models

import android.graphics.Bitmap
import java.io.Serializable

data class Adventurer (
    var advFirstName: String,
    var advSurname: String,
    var advEmail: String,
    var advTelephone: String,
    var advPoints: Double,
    var advActive: Boolean,
    var advAdmin: Boolean,
    var advTotalSpent: Double,
    var advGoldenBootCount: Int) : Serializable
{
    private var avdID: Int = 0
    private var advPicLink = ""
    private var advPic: Bitmap? = null

    fun setID(id: Int){
        avdID = id
    }

    fun getID(): Int{
        return avdID
    }

    fun setPicLink(link: String){
        advPicLink = link
    }

    fun getPicLink() : String{
        return advPicLink
    }

    fun setPic(pic: Bitmap?){
        advPic = pic
    }

    fun getPic() : Bitmap?{
        return advPic
    }

    fun updateDetails(name: String, surname: String, email: String, phone: String){
        advFirstName = name
        advSurname = surname
        advEmail = email
        advTelephone = phone
    }
}