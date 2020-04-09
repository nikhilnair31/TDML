package com.appnamenull.mlscheduler.Utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Cloud{
    fun pushString(string : String){
        val database = Firebase.database
        //val date = Time.database
        val myRef = database.getReference("message")
        myRef.setValue(string)
    }
}