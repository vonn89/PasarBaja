package com.excellentsystem.PasarBaja;

import java.sql.Connection;
import java.sql.DriverManager;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Xtreme
 */
public class Koneksi {
    public static Connection getConnection()throws Exception{
//        String DbName = "jdbc:mysql://localhost:3306/auristeel?"
//                + "connectTimeout=10000&socketTimeout=10000&autoReconnect=true";
//        Class.forName("com.mysql.jdbc.Driver");
//        return DriverManager.getConnection(DbName,"admin","excellentsystem");

        
        String DbName = "jdbc:mysql://34.87.153.33:3306/pasarbaja?"
                + "connectTimeout=10000&socketTimeout=10000&autoReconnect=true";
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(DbName,"root","yunaz051189");
    }
}
