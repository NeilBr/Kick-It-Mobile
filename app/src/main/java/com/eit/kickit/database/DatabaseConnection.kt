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
            res = DriverManager.getConnection("jdbc:mysql://kickit-db.cqqbwmmosjza.eu-west-1.rds.amazonaws.com:3306/kickit", "kickitadmin", "kickitwrr302")
            println("Connection Made")
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return res
    }
}