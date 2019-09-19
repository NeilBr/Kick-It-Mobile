package com.eit.kickit.database

import android.os.AsyncTask
import java.sql.*

class Database {

    private var connection: Connection? = null
    private var statement: PreparedStatement? = null
    private var select: Boolean = true

    private fun createConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            connection = DriverManager.getConnection("jdbc:mysql://kickit-db.cqqbwmmosjza.eu-west-1.rds.amazonaws.com:3306/kickit", "kickitadmin", "kickitwrr302")
            println("Connection Made")
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun runQuery(query: String, select: Boolean, callback: (Any) -> Unit){
        this.select = select
        Query(callback).execute(query)
    }

    private inner class Query(callback: (Any) -> Unit) : AsyncTask<String, Void, Any>(){

        private var cb = callback

        override fun doInBackground(vararg p0: String): Any {
            try{
                createConnection()
                statement = connection!!.prepareStatement(p0[0])

                return when(select){
                    true -> statement!!.executeQuery()
                    false -> statement!!.execute()
                }
            }
            catch(ex: Exception){
                return ex.message!!
            }
        }

        @Override
        override fun onPostExecute(result: Any) {
            cb.invoke(result)
        }
    }
}