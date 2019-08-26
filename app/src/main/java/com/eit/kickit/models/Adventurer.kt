package com.eit.kickit.models

data class Adventurer (
    var advFirstName: String,
    var advSurname: String,
    var advEmail: String,
    var advTelephone: String,
    var advPoints: Double,
    var advActive: Boolean,
    var advAdmin: Boolean)
{
    var avdID: Int = 0

    public fun setID(id: Int){
        avdID = id
    }

    public fun getID(): Int{
        return avdID
    }

    fun updateDetails(name: String, surname: String, email: String, phone: String){
        advFirstName = name
        advSurname = surname
        advEmail = email
        advTelephone = phone
    }
}