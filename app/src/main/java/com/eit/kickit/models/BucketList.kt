package com.eit.kickit.models

/**
 *  Data class for individual bucket list
 */
data class BucketList (
    var blID : Int = 0,
    var blName : String,
    var blDescription : String,
    var blReqPoints : Int
)
{
    public fun setID(id: Int){
        blID = id
    }

    public fun getID(): Int{
        return blID
    }
}