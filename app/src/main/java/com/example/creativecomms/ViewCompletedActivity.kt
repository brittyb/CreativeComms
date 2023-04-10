package com.example.creativecomms

import android.app.DownloadManager

import android.content.Context

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity



class ViewCompletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_completed)

        var nameView = findViewById<TextView>(R.id.fileNameText)
        var notesView = findViewById<TextView>(R.id.notesText)
        val downloadButton = findViewById<Button>(R.id.downloadCommButton)

        if(intent.extras != null){
            val extras = intent.extras
            val completed = extras?.getSerializable("CompletedComm") as CompletedComm
            notesView.text = completed.notes
            nameView.text = completed.fileName

            downloadButton.setOnClickListener {
                val fileUri = completed.fileUri
                val filename = completed.fileName
                downloadFile(fileUri!!, filename!!, this)
            }
        }




    }

}

private fun downloadFile(uri : String, filename : String, context: Context) {


    val request = DownloadManager.Request(Uri.parse(uri))
        .setTitle(filename)
        .setDescription("Your commission is being downloaded")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setAllowedOverMetered(true)

    var dm : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

dm.enqueue(request)




}

