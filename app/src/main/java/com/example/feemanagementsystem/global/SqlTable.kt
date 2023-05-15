package com.example.feemanagementsystem.global

object SqlTable {
    const val admin = "CREATE TABLE ADMIN(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "USER_NAME TEXT DEFAULT '',"+
            "PASSWORD TEXT DEFAULT ''," +
            " MOBILE TEXT DEFAULT'')"


    const val member= "CREATE TABLE MEMBER(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "FIRST_NAME TEXT DEFAULT''," +
            "LAST_NAME TEXT DEFAULT ''," +
            "GENDER TEXT DEFAULT'', " +
            "CLASS TEXT DEFAULT ''," +
            "REGISTER_NUMBER TEXT DEFAULT''," +
            "MOBILE TEXT DEFAULT '',"+
            "SECTION TEXT DEFAULT''," +
            "DATE_OF_JOINING TEXT DEFAULT''," +
            "MONTHLY TEXT DEFAULT''," +
            "REPAYMENT TEXT DEFAULT'',"+
            "DISCOUNT TEXT DEFAULT''," +
            "TOTAL TEXT DEFAULT'', " +
            "IMAGE_PATH TEXT DEFAULT''," +
            "STATUS TEXT DEFAULT 'A')"

    const val fee = "CREATE TABLE FEE(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "ONE_MONTH TEXT DEFAULT'', " +
            "THREE_MONTH TEXT DEFAULT '',"+
            "SIX_MONTH TEXT DEFAULT''," +
            "NINE_MONTH TEXT DEFAULT''," +
            "TWELVE_MONTH TEXT DEFAULT '')"
}