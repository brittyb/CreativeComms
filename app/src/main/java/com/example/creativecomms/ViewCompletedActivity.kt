package com.example.creativecomms

import android.os.Bundle
import android.util.Log
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
        }

        downloadButton.setOnClickListener {


        }


    }

}