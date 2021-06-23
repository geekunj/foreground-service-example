package com.example.foregroundserviceexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {


    override fun onResume() {
        getFileContents()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ContextCompat.startForegroundService(this, Intent(this, LogWritingService::class.java).apply {
            startService(this)
        })

    }

    private fun getFileContents(){
        var fileReader: FileReader? = null
        val file = File(getExternalFilesDir("geek-data"), "log-file.txt")
        val stringBuilder = StringBuilder()
        try {
            fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            var line = bufferedReader.readLine()
            while (line != null){
                stringBuilder.append(line).append('\n')
                line = bufferedReader.readLine()
            }
        }catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }finally {
            val fileContents = stringBuilder.toString()
            tv_logfile_content.text = fileContents
        }
    }


}