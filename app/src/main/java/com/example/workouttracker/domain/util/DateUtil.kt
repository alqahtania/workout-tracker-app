package com.example.workouttracker.domain.util

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil
@Inject
constructor(
private val dateFormat: SimpleDateFormat
)
{

    fun removeTimeFromDateString(sd : String) : String{
        return sd.substring(0, sd.indexOf(" "))
    }


    fun getCurrentTimestamp() : String{
        return dateFormat.format(Date())
    }

    fun convertFromDbEntityTimeToStringDate(timeInMilis : Long) : String{
        return dateFormat.format(Date(timeInMilis))
    }

    fun convertToDbEntityTimeToMillis(date : String) : Long{
        val parseDate = dateFormat.parse(date)
        return parseDate.time
    }
}