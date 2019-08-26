package com.eit.kickit.database

import java.sql.*
import java.util.Properties

class DatabaseConnection{

    fun createConnection(): Connection?{

        var res: Connection? = null

        val connectionProps = Properties()
        connectionProps.put("user", "root")
        connectionProps.put("password", "604255hdd")
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            res = DriverManager.getConnection("jdbc:mysql://10.0.0.27:3306/test", "root", "604255hdd")
            println("Connection Made")
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return res
    }
}