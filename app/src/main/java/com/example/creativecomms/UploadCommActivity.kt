package com.example.creativecomms

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.*

class UploadCommActivity : AppCompatActivity() {
    private var uri: Uri? = null
    private var fileName : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_comm)

        val fileButton = findViewById<Button>(R.id.uploadFileButton)
        val commButton = findViewById<Button>(R.id.downloadCommButton)

        fileButton.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)

        }

        commButton.setOnClickListener {
            if(uri!=null){
                uploadFileToFirebaseStorage()
            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            uri = data?.data // The URI with the location of the file
            Log.d("UploadActivity", "URI: $uri")
            fileName = getFileName(uri!!).toString()
            val fileNameText = findViewById<TextView>(R.id.fileNameText)
            fileNameText.text = fileName
            Log.d("UploadActivity", "Path: $fileName")
        }
    }



    private fun uploadFileToFirebaseStorage() {
        if(uri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("completedComms/$filename")
        ref.putFile(uri!!)
            .addOnSuccessListener { it ->
                Log.d("UploadActivity", "Successfully uploaded comm: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("UploadActivity", it.toString())
                    saveFileToFirebase(it.toString())
                }
            }
    }
    private fun Context.getFileName(uri: Uri): String? = when(uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }

    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
        }
    }.getOrNull()
    private fun saveFileToFirebase(uri : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val extras = intent
        if(extras!=null){
            val request = extras.getSerializableExtra("Request") as Request
            val reqID = request.reqID
            val notesText = findViewById<TextView>(R.id.commNotesText).text.toString()
            val title = extras.getStringExtra("Title")
            val requesterID = request.requesterUID
            val completedComm = CompletedComm(notesText, uri, title,requesterID,uid, request.reqID,
                request.paid!!, fileName
            )
            val ref = FirebaseDatabase.getInstance().getReference("Completed/${request.requesterUID}/${reqID}")
            ref.setValue(completedComm).addOnSuccessListener { Log.d("UploadActivity", "Successfully set value")
                setCommToCompleted(request)
        }


         }
    }

    private fun setCommToCompleted(request : Request) {
        var ref = FirebaseDatabase.getInstance().
            getReference("/Requests/${request.requesterUID}/${request.reqID}/completed")
        ref.setValue(true).addOnSuccessListener { Log.d("UploadActivity", "Successfully set request as completed") }

        ref = FirebaseDatabase.getInstance().
        getReference("/ArtistRequests/${request.artistUID}/${request.reqID}/completed")
        ref.setValue(true).addOnSuccessListener { Log.d("UploadActivity", "Successfully set artist request as completed") }
    }

}