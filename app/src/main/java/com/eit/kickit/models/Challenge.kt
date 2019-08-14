package com.eit.kickit.models

/**
 * Data class for individual Challenge
 */
data class Challenge(
    var cID : Int,
    var cName : String,
    var cDescription : String,
    var cPoints : Int,
    var cPrice : Double,
    var cStatus : Boolean
)

{
    public fun setID(id: Int){
        cID = id
    }

    public fun getID(): Int{
        return cID
    }
}