package com.appnamenull.mlscheduler.Utils

import android.content.SharedPreferences

object Saving {

    fun saveLists(prefh : SharedPreferences, listh : List<Int>, nameh : String,
                  preft : SharedPreferences, listt : List<Int>, namet : String,
                  prefb : SharedPreferences, listb : List<Int>, nameb : String){
        //convert list to string separated by comma
        val strh = listh.joinToString()
        val strt = listt.joinToString()
        val strb = listb.joinToString()
        //push each string to a shared preference
        prefh.edit().putString(nameh, strh).apply()
        preft.edit().putString(namet, strt).apply()
        prefb.edit().putString(nameb, strb).apply()
        //display
        println("saveLists::$strh")
        println("saveLists::$strt")
        println("saveLists::$strb")
    }

    fun loadLists(prefh : SharedPreferences, listh : MutableList<Int>, nameh : String,
                  preft : SharedPreferences, listt : MutableList<Int>, namet : String,
                  prefb : SharedPreferences, listb : MutableList<Int>, nameb : String){
        //get string from shared prefs based on names
        val strh = prefh.getString(nameh, "")
        val strt = preft.getString(namet, "")
        val strb = prefb.getString(nameb, "")
        //display
        println("loadLists--$strh")
        println("loadLists--$strt")
        println("loadLists--$strb")
        //add each part of string seperated by comma to list and typecast to long while checking for empty ""
        if (strh != null && strt != null && strb != null) {
            var parts = strh.split(",")
            for (i in parts.indices)
                if (parts[i] != " ")
                    listh.add(parts[i].trim().toInt())
            parts = strt.split(",")
            for(i in parts.indices)
                if(parts[i] != " ")
                    listt.add(parts[i].trim().toInt())
            parts = strb.split(",")
            for(i in parts.indices)
                if(parts[i] != " ")
                    listb.add(parts[i].trim().toInt())
        }
    }

    fun clearLists(prefh : SharedPreferences, nameh : String, preft : SharedPreferences, namet : String, prefb : SharedPreferences, nameb : String){
        prefh.edit().remove(nameh).apply()
        preft.edit().remove(namet).apply()
        prefb.edit().remove(nameb).apply()
    }
}