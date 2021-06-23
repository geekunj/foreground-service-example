package com.example.foregroundserviceexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LogWritingService: Service() {

    private val serviceStartTime = getCurrentTime()

    private val notification by lazy {
        NotificationCompat.Builder(this, "log_writing_channel_id")
            .setSmallIcon(R.drawable.ic_notes)
            .setContentTitle("Writing log...")
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(12345, notification)
        CoroutineScope(Dispatchers.IO).launch{
            if(isExternalStorageAvailable()){
                //logs timestamp in every 1 minute for a total of 5 minutes before the service ends
                val updateTimer = Timer()
                updateTimer.schedule(object: TimerTask(){
                    override fun run() {
                        if(getTimeDifferenceInMinutes() != null){
                            if(getTimeDifferenceInMinutes() != 5){
                                logTimeStampsInFile()
                            }else if(getTimeDifferenceInMinutes() == 5){
                                stopForeground(true)
                                updateTimer.cancel()
                            }
                        }

                    }

                }, 0, 60000)

            }



        }
    }

    override fun onBind(intent: Intent?) = null



    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationCannel = NotificationChannel("log_writing_channel_id", "log_writing_channel", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationCannel)
        }
    }

    private fun isExternalStorageAvailable() = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)

    private fun getCurrentTimeStamp(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.US)
        val currentDate: String = sdf.format(Date())

        return "$currentDate\n"
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.US)
        return sdf.format(Date())
    }

    private fun logTimeStampsInFile(){
        val logFile = File(getExternalFilesDir("geek-data"), "log-file.txt")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(logFile, true)
            fos.write(getCurrentTimeStamp().toByteArray())
            fos.close()
        }catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun getTimeDifferenceInHours():Int?{
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.US)
        val hours = try {
            val initialDate = sdf.parse(serviceStartTime)
            val finalDate = sdf.parse(getCurrentTime())
            val difference = finalDate.time - initialDate.time
            (difference/(1000 * 60 * 60)).toInt()
        } catch (e: Exception) {
            null
        }
        return hours
    }

    private fun getTimeDifferenceInMinutes():Int?{
        Log.d("LogWritingService", "getTimeDifferenceInMinutes: ServiceStartTime: $serviceStartTime")
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.US)
        val minutes = try {
            val initialDate = sdf.parse(serviceStartTime)
            val finalDate = sdf.parse(getCurrentTime())
            Log.d("LogWritingService", "getTimeDifferenceInMinutes: Current Time: ${getCurrentTime()}")
            val difference = finalDate.time - initialDate.time
                    ((difference/(1000 * 60)) % 60).toInt()
        } catch (e: Exception) {
            null
        }
        Log.d("LogWritingService", "getTimeDifferenceInMinutes: difference: $minutes")
        return minutes
    }

}