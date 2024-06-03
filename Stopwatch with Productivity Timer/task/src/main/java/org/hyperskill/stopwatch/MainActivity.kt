package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat



const val CHANNEL_ID = "org.hyperskill"
class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val textTime = findViewById<TextView>(R.id.textView)
        val handler = Handler(Looper.getMainLooper())
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = ProgressBar.INVISIBLE
        val listOfColors = listOf(Color.GREEN, Color.BLUE, Color.RED, Color.MAGENTA, Color.GRAY)
        settingsButton.isEnabled = true


        val name = "time"
        val descriptionText = "time"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(applicationContext, 393939, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pIntent)
            .setAutoCancel(true)

        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_ONLY_ALERT_ONCE

        var isRunning = false
        var sec = 0
        var upperLimit = 0

        val updateSeconds : Runnable = object : Runnable {
            override fun run() {
                val minutes = (sec % 3600) / 60
                val seconds = sec % 60
                val time = String.format("%02d:%02d", minutes, seconds)
                textTime.text = time


                if (isRunning) {
                    sec++
                    progressBar.indeterminateTintList = ColorStateList.valueOf(listOfColors[sec % 5])
                }

                if (sec > upperLimit && upperLimit != 0) {
                    textTime.setTextColor(Color.RED)
                    if (upperLimit > 0) {
                        notificationManager.notify(393939, notification)
                    }
                }

                handler.postDelayed(this, 1000)
            }
        }

        startButton.setOnClickListener {
            settingsButton.isEnabled = false
            if (!isRunning) {
                isRunning = true
                progressBar.visibility = ProgressBar.VISIBLE
                handler.post(updateSeconds)
            }
        }

        resetButton.setOnClickListener {
            settingsButton.isEnabled = true
            handler.removeCallbacks(updateSeconds)
            isRunning = false
            sec = 0
            textTime.text = "00:00"
            textTime.setTextColor(Color.BLACK)
            progressBar.visibility = ProgressBar.INVISIBLE
            notificationManager.cancelAll()
            notificationManager.cancelAll()
        }

        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_custom, null, false)
            AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton("Ok") {_, _ ->
                    val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                    if (editText.text.isNotEmpty()) {
                        upperLimit = editText.text.toString().toInt()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

}

