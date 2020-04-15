package com.appnamenull.mlscheduler.Utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Chunk(dateStr: String, lines: String) {
    var dateStr : String = dateStr
    var lines : String = lines
}

object Cloud{

    var firebaseRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("TotalUsageNetworkStats");

    fun pushString(dateString : String, fullText : String){
        val mDatabase : DatabaseReference = firebaseRef
        val strs = fullText.split("\n").toTypedArray()

        //mDatabase.child(dateString)
        val userId = mDatabase.push().key
        for(i in 0 until 24) {
            //mDatabase.child(strs[0].substring(0, 24)).setValue(strs[0])
            val chunk = Chunk(dateString, strs[i])
            if (userId != null) {
                mDatabase.child(userId).setValue(chunk)
            };
        }
    }
}